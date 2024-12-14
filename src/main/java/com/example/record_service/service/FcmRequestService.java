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

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmRequestService {
    private final ObjectMapper objectMapper;

    public String sendMessage(FcmRequestDto requestDto) throws JsonProcessingException, FirebaseMessagingException {
        // FcmRequestDto.Body -> json으로 변환
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
    }
}

