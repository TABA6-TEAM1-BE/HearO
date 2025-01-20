package com.example.user_service.service;

import com.example.user_service.entity.Member;
import com.example.user_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final CounterService counterService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public ResponseEntity<Map<String, String>> signUp(String username, String password) {
        try {
            Optional<Member> user = memberRepository.findByUsername(username);

            // 1. 사용자 이미 존재
            if (user.isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "User already exists");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400 Bad Request
            }

            // 2. 자동 증가 idx 생성
            long idx = counterService.getNextUserIdxSequence("member_idx");

            // 3. 회원 정보 저장
            Member member = new Member();
            member.setUsername(username);
            member.setPassword(password);
            member.setIdx(String.valueOf(idx)); // idx를 문자열로 변환하여 설정
            ArrayList<String> roles = new ArrayList<>();
            roles.add("USER");
            member.setRoles(roles);

            memberRepository.save(member);

            // 4. 성공 응답
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sign up successful");
            return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
        } catch (Exception e) {
            // 5. 서버 에러 처리
            Map<String, String> response = new HashMap<>();
            response.put("message", "Internal server error");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error
        }
    }

/*
    @Transactional
    public ResponseEntity<?> authenticate(String username, String password) {
        try {
            // 1. username + password를 기반으로 Authentication 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            log.info("Step 1: AuthenticationToken Generate - username: {}", username);

            // 2. 실제 인증 수행
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.info("Step 2: Authentication Success - isAuthenticated: {}", authentication.isAuthenticated());

            // 3. DB에서 사용자 정보 가져오기
            Optional<Member> memberOptional = memberRepository.findByUsername(username);
            if (memberOptional.isEmpty()) {
                throw new RuntimeException("User not found in database");
            }
            Member member = memberOptional.get();

            // 4. Redis 에 사용자 정보 저장
            String redisKey = "member:" + member.getIdx(); // Redis 키 생성
            redisTemplate.delete(redisKey); // 기존 키 삭제 (타입 충돌 방지)

            redisTemplate.opsForHash().put(redisKey, "id", member.getId());
            redisTemplate.opsForHash().put(redisKey, "idx", member.getIdx());
            redisTemplate.opsForHash().put(redisKey, "username", member.getUsername());
            redisTemplate.opsForHash().put(redisKey, "password", member.getPassword());
            redisTemplate.opsForHash().put(redisKey, "roles", member.getRoles());

            log.info("Stored user info in Redis with key: {}", redisKey);

            // 5. Redis 키를 HTTP 응답 바디에 넣어서 반환
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("redisKey", redisKey);
            return ResponseEntity.ok(responseBody); // 200 OK와 함께 응답

        } catch (Exception e) {
            log.error("Authentication failed for user: {}", username, e);

            // 에러 메시지를 HTTP Body에 담아 401 Unauthorized 반환
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

 */

    @Transactional
    public Authentication signIn(String username, String password) {
        // 1. username + password를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        log.info("Step 1: AuthenticationToken Generate - username: {}", username);

        // 2. authenticate() 메서드 실행 (CustomUserDetailsService.loadUserByUsername 호출)
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("Step 2: Authentication Success - isAuthenticated: {}", authentication.isAuthenticated());
        log.info("{}", authentication);

        return authentication;
    }

}

