package com.example.record_service.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OpenAiRequestDto {
    private String model;
    private List<OpenAiMessageDto> messages;

    public OpenAiRequestDto(String model, String prompt) {
        this.model = model;
        this.messages =  new ArrayList<>();
        this.messages.add(new OpenAiMessageDto("user", prompt));
    }
}
