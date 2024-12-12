package com.example.record_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FcmRequestDto {
    private String fcmToken;

    private String title;

    private String body;
}
