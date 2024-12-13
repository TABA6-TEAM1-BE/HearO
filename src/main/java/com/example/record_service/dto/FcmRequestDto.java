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
    private String title;
    private Body body;          // 중첩 클래스 Body 로 AiResultDto 를 포함시킴

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Body {
        private String recordIdx;
        private String result;
        private Boolean isHuman;
        private String text;
    }
}
