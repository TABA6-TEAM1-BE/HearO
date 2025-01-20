package com.example.user_service.service;

import com.example.user_service.entity.Member;
import com.example.user_service.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    CounterService counterService;

    @Mock
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("회원가입")
    void signUp() {
        // 1. 입력 값 준비
        String username = "username";
        String password = "password";

        // 2. Mock 동작 정의
        when(memberRepository.findByUsername(username))
                .thenReturn(Optional.of(new Member())); // 사용자 이미 존재

        lenient().when(counterService.getNextUserIdxSequence("member_idx"))
                .thenReturn(Long.valueOf(1));

        lenient().when(memberRepository.save(Mockito.any(Member.class)))
                .thenReturn(new Member());

        // 3. 메서드 실행
        ResponseEntity<?> response = memberService.signUp(username, password);

        // 4. 결과 검증
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // 400 Bad Request
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("User already exists", body.get("message"));

    }

//    @Test
//    void signIn() {
//    }
}