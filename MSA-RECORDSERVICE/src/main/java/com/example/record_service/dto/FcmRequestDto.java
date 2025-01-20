package com.example.record_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FcmRequestDto {
    private String fcmToken;
    private Notification notification;
    private Data data;  // 클라이언트에 전달할 데이터

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Notification {
        private String title;  // 알림 제목
        private String body;   // 알림 본문
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Data {
        private String recordIdx;
        private String result;
        private Boolean isHuman;
        private String text;
    }
}

