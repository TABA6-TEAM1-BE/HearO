package com.example.record_service.controller;

import com.example.record_service.dto.AiResultDto;
import com.example.record_service.dto.FcmRequestDto;
import com.example.record_service.entity.Record;
import com.example.record_service.repository.UserServiceClient;
import com.example.record_service.service.FcmRequestService;
import com.example.record_service.service.OpenAiService;
import com.example.record_service.service.RecordService;
import com.google.firebase.messaging.FirebaseMessagingException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/records/ai")
@RequiredArgsConstructor
public class AiResultController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RecordService recordService;
    private static final Logger log = LoggerFactory.getLogger(AiResultController.class);
    private final UserServiceClient userServiceClient;
    private final FcmRequestService fcmRequestService;
    private final OpenAiService openAiService;

    // Ai 모델에게서 해당 HTTP 요청을 보내서 결과값 받아옴
    @PostMapping("/results")
    public ResponseEntity<String> receiveAIResult(@RequestBody AiResultDto resultDto) {
        try {
            String recordIdx = resultDto.getRecordIdx(); // 기록 식별
            String result = resultDto.getResult(); // AI 결과값

            Boolean isHuman = resultDto.getIsHuman();
            String text = isHuman ? resultDto.getText() : null; // isHuman일 경우에만 text 값 저장

            String prompt = "이 문장에서 틀린 맞춤법과 띄어쓰기를 올바르게 고쳐주고 문맥이 자연스럽게 수정해줘. " +
                    "그리고 출력은 내가 제공한 해당 문장만 고쳐서 나오게 해주고 앞 뒤에 다른 추가 설명은 필요없어. : ";

            // OpenAI 후처리 (isHuman 인 경우)
            if (isHuman && text != null && !text.isEmpty()) {
                Map<String, Object> aiResponse = openAiService.getChatResponse(prompt + text).getBody();
                if (aiResponse != null && aiResponse.containsKey("data")) {
                    text = (String) aiResponse.get("data");
                    log.info("Processed text from OpenAI: {}", text);
                } else {
                    log.warn("OpenAI response was null or did not contain data.");
                }
            }

            // Record 업데이트
            if (!recordService.updateRecordWithAIResult(recordIdx, result, isHuman, text)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record with recordIdx " + recordIdx + " not found.");
            }

            Optional<Record> optionalRecord = recordService.getRecordByRecordIdx(recordIdx);
            String userIdx = optionalRecord.get().getUserIdx();

            // RedisMember 에서 userIdx 로 deviceToken 조회
            String deviceToken = userServiceClient.getMemberByUserIdx(userIdx).getBody().getFcmToken();
            log.info("User Device Token: {}", deviceToken);

            // FcmRequestDto 생성
            FcmRequestDto fcmRequest = FcmRequestDto.builder()
                    .fcmToken(deviceToken)
                    .notification(FcmRequestDto.Notification.builder()
                            .title("우리 앱 이름")
                            .body(result + "에 대한 알림이 도착했습니다!")
                            .build())
                    .data(FcmRequestDto.Data.builder()
                            .recordIdx(recordIdx)
                            .result(result)
                            .isHuman(isHuman)
                            .text(text)
                            .build())
                    .build();
            log.info("FCM Request: {}", fcmRequest);

            // FCM 메시지 전송
            // String fcmResponse = fcmRequestService.sendMessage(deviceToken, resultDto);
            String fcmResponse = fcmRequestService.sendMessage(fcmRequest);
            log.info("FCM Response: {}", fcmResponse);

            return ResponseEntity.ok("FCM message sent successfully.");

            /*
            // WebSocket 경로로 메시지 전송
            String destination = "/socket/" + userIdx;
            messagingTemplate.convertAndSend(destination, result);

            log.info("WebSocket message sent to {}: {}", destination, result);
            return ResponseEntity.ok("Message sent to WebSocket");

             */
        } catch (FeignException.Unauthorized e) {
            log.error("Unauthorized: User DeviceToken is not found: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } catch (FirebaseMessagingException e) {
            log.error("Error sending FCM message: {}", e.getMessage(), e);
            // FirebaseMessagingException 에서 오류에 해당하는 HTTP 상태코드 반환
            return ResponseEntity.status(HttpStatus.valueOf(e.getHttpResponse().getStatusCode()))
                    .body("Failed to send FCM message: " + e.getMessage());
        }
        catch (Exception e) {
            log.error("Error while sending Fcm message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send Fcm message");
        }
    }
}
