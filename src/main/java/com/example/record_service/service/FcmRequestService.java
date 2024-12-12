package com.example.record_service.service;

import com.example.record_service.dto.FcmRequestDto;
import com.example.record_service.repository.UserServiceClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmRequestService {
    private final UserServiceClient userServiceClient;

    public String sendMessage(FcmRequestDto requestDto) {
        try {
            // FCM 메시지 구성
            Message message = Message.builder()
                    .putData("title", requestDto.getTitle())
                    .putData("body", requestDto.getBody())
                    .setToken(requestDto.getFcmToken()) // 대상 디바이스 토큰 설정
                    .build();

            // 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Message sent successfully. FCM response: {}", response);
            return response;
        } catch (FirebaseMessagingException e) {
            log.error("Error sending FCM message: {}", e.getMessage(), e);
            return "Failed to send FCM message: " + e.getMessage();
        }
    }
}
