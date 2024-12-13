package com.example.record_service.service;

import com.example.record_service.dto.FcmRequestDto;
import com.example.record_service.repository.UserServiceClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmRequestService {
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;

    public String sendMessage(FcmRequestDto requestDto) {
        try {
            // FcmRequestDto.Body -> json 으로 변환
            String jsonBody = objectMapper.writeValueAsString(requestDto.getBody());
            log.info("Before: {}", requestDto.getBody());
            log.info("FcmRequest Body: {}", jsonBody);

            // FCM 메시지 생성
            Message message = Message.builder()
                    .setToken(requestDto.getFcmToken())
                    .putData("title", requestDto.getTitle())
                    .putData("body", jsonBody) // json 형식으로 전송
                    .build();
            log.info("Message: {}", message);

            // 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Message sent successfully. FCM response: {}", response);

            return response;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Failed to process JSON: " + e.getMessage();
        }catch (FirebaseMessagingException e) {
            log.error("Error sending FCM message: {}", e.getMessage(), e);
            return "Failed to send FCM message: " + e.getMessage();
        }
    }
}
