package com.example.record_service.controller;

import com.example.record_service.dto.AiResultDto;
import com.example.record_service.dto.FcmRequestDto;
import com.example.record_service.entity.Record;
import com.example.record_service.repository.RecordRepository;
import com.example.record_service.repository.UserServiceClient;
import com.example.record_service.service.CounterService;
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

import java.time.LocalDateTime;
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
    private final CounterService counterService;
    private final RecordRepository recordRepository;

    // Ai 모델에게서 해당 HTTP 요청을 보내서 결과값 받아옴
    @PostMapping("/results")
    public ResponseEntity<String> receiveAIResult(@RequestBody AiResultDto resultDto) {
        try {
            String recordIdx = resultDto.getRecordIdx(); // 기록 식별
            String result = resultDto.getResult(); // AI 결과값

            Boolean isHuman = resultDto.getIsHuman();
            String text = isHuman ? resultDto.getText() : null; // isHuman일 경우에만 text 값 저장

            String prompt = "이 문장에서 중요 내용만 정리해서 요약해줘.  " +
                    "앞 뒤에 다른 추가 설명은 필요없어. : ";

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

            if(result.contains(",")){
                String [] resultArr = result.split(",");
                // Record 업데이트
                if (!recordService.updateRecordWithAIResult(recordIdx, resultArr[0], false, null)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record with recordIdx " + recordIdx + " not found.");
                }


                Optional<Record> optionalRecord = recordService.getRecordByRecordIdx(recordIdx);
                String userIdx = optionalRecord.get().getUserIdx();

                //새로 레코드 추가
                // 자동 증가 recordIdx 생성
                long newRecordIdx = counterService.getNextRecordIdxSequence("record_idx");
                String newrecordIdx = String.valueOf(newRecordIdx);
                // 현재 시간 저장
                LocalDateTime time = LocalDateTime.now();
                // Record 생성 후 저장
                Record record = new Record(userIdx,resultArr[1],time);
                record.setRecordIdx(String.valueOf(newrecordIdx)); // 자동 생성된 recordIdx
                record.setText(text);
                recordRepository.save(record);




                // RedisMember 에서 userIdx 로 deviceToken 조회
                String deviceToken = userServiceClient.getMemberByUserIdx(userIdx).getBody().getFcmToken();
                log.info("User Device Token: {}", deviceToken);

                // FcmRequestDto 생성
                FcmRequestDto fcmRequest = FcmRequestDto.builder()
                        .fcmToken(deviceToken)
                        .notification(FcmRequestDto.Notification.builder()
                                .title("HearO")
                                .body(result + "에 대한 알림이 도착했습니다!")
                                .build())
                        .data(FcmRequestDto.Data.builder()
                                .recordIdx(recordIdx)
                                .result(resultArr[0])
                                .isHuman(false)
                                .text(null)
                                .build())
                        .build();
                log.info("FCM Request: {}", fcmRequest);

                // FCM 메시지 전송
                // String fcmResponse = fcmRequestService.sendMessage(deviceToken, resultDto);
                String fcmResponse = fcmRequestService.sendMessage(fcmRequest);
                log.info("FCM Response: {}", fcmResponse);


                // FcmRequestDto2 생성
                FcmRequestDto fcmRequest2 = FcmRequestDto.builder()
                        .fcmToken(deviceToken)
                        .notification(FcmRequestDto.Notification.builder()
                                .title("HearO")
                                .body(result + "에 대한 알림이 도착했습니다!")
                                .build())
                        .data(FcmRequestDto.Data.builder()
                                .recordIdx(newrecordIdx)
                                .result(resultArr[1])
                                .isHuman(isHuman)
                                .text(text)
                                .build())
                        .build();
                log.info("FCM Request: {}", fcmRequest2);

                // FCM 메시지 전송
                // String fcmResponse2 = fcmRequestService.sendMessage(deviceToken, resultDto);
                String fcmResponse2 = fcmRequestService.sendMessage(fcmRequest2);
                log.info("FCM Response: {}", fcmResponse2);


                return ResponseEntity.ok("FCM message sent successfully.");


            }else{
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
                                .title("HearO")
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
            }



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



