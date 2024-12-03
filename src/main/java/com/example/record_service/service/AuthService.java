package com.example.record_service.service;

import com.example.record_service.repository.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    private final UserServiceClient userServiceClient;

    public AuthService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    /*
    public boolean validateUser(String idx) {
        try {
            ResponseEntity<String> response = userServiceClient.validateIdxByRedis(idx);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            // 인증 실패 시 로그 및 처리
            log.error("Failed to validate user with idx: {}", idx, e);
            return false;
        }
    }

    public String getUsernameFromUserService(String idx) {
        ResponseEntity<String> response = userServiceClient.getUsernameFromRedis(idx);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to retrieve username for idx: " + idx);
        }
        return response.getBody();
    }


     */

}
