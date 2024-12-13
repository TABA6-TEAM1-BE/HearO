package com.example.record_service.service;

import com.example.record_service.dto.OpenAiRequestDto;
import com.example.record_service.dto.OpenAiResponseDto;
import com.example.record_service.entity.OpenAiResult;
import com.example.record_service.repository.OpenAiResponseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OpenAiService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    private final RestTemplate restTemplate;
    private final OpenAiResponseRepository openAiResponseRepository;

    public OpenAiService(RestTemplate restTemplate, OpenAiResponseRepository openAiResponseRepository) {
        this.restTemplate = restTemplate;
        this.openAiResponseRepository = openAiResponseRepository;
    }

    public ResponseEntity<Map<String, Object>> getChatResponse(String prompt) {
        Map<String, Object> response = new HashMap<>();

        try {
            // OpenAI API 요청
            OpenAiRequestDto request = new OpenAiRequestDto(model, prompt);
            OpenAiResponseDto openAiResponse = restTemplate.postForObject(apiURL, request, OpenAiResponseDto.class);

            // OpenAI 응답이 유효한지 확인
            if (openAiResponse != null && openAiResponse.getChoices() != null && !openAiResponse.getChoices().isEmpty()) {
                String chatResponse = openAiResponse.getChoices().get(0).getMessage().getContent();

                // 응답을 MongoDB에 저장
                saveResponseToDatabase(prompt, chatResponse);

                // 성공 메시지 설정
                response.put("success", true);
                response.put("message", "Chat response received successfully.");
                response.put("data", chatResponse);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                // 응답이 없는 경우 처리
                response.put("success", false);
                response.put("message", "No response from OpenAI.");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            // 예외 처리
            response.put("success", false);
            response.put("message", "Failed to get chat response. Error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveResponseToDatabase(String prompt, String response) {
        OpenAiResult document = new OpenAiResult();
        document.setPrompt(prompt);
        document.setResponse(response);

        openAiResponseRepository.save(document);
    }
}
