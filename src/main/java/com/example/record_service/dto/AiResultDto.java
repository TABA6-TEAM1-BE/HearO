package com.example.record_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResultDto {
    private String recordIdx;
    private String result;
    private Boolean isHuman = false;
    private String text = null; // isHuman 일 경우 stt 결과값 저장
}
