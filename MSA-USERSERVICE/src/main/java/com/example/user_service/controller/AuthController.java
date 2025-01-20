package com.example.user_service.controller;

import com.example.user_service.service.AuthService;
import com.example.user_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final MemberService memberService;
    private final AuthService authService;


    /*
    // Redis 에 해당 idx 유저가 있는지 검증
    @GetMapping("/{idx}")
    public ResponseEntity<String> validateIdxByRedis(@PathVariable String idx) {
        String username = authService.validateIdxByRedis(idx);
        log.info("Idx from header: {}", username);
        if (username != null) {
            return ResponseEntity.ok(username);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid idx");
        }
    }

    // Redis 에서 username 가져오기
    @GetMapping("/username/{idx}")
    public ResponseEntity<String> getUsernameFromRedis(@PathVariable String idx) {
        String username = authService.getUsernameFromRedis(idx);
        log.info("Username from header: {}", username);
        if (username != null) {
            return ResponseEntity.ok(username);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid username");
        }
    }

    @GetMapping("/{userIdx}/username")
    public ResponseEntity<String> getUsernameByUserIdx(@PathVariable String userIdx) {
        try {
            String username = authService.getUsernameByUserIdx(userIdx);
            if (username != null) {
                return ResponseEntity.ok(username);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            log.error("Error retrieving username for userIdx {}: {}", userIdx, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving the username");
        }
    }

     */


}
