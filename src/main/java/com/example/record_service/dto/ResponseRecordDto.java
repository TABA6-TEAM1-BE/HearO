package com.example.record_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResponseRecordDto {
    private String deviceType;

    private String text = null;

    private LocalDateTime resultTime;
}
