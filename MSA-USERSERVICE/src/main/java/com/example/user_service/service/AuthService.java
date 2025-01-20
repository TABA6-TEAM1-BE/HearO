package com.example.user_service.service;

import com.example.user_service.entity.Member;
import com.example.user_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;

    /*
    // 유효한 idx 인지 검증
    public String validateIdxByRedis(String memberIdx) {
        // Redis 키 생성 (e.g., "member:1")
        String redisKey = "member:" + memberIdx;

        try {
            // Redis 에서 idx 필드 가져오기
            String idx = (String) redisTemplate.opsForHash().get(redisKey, "idx");
            log.info("Retrieved idx: {} for redisKey: {}", idx, redisKey);
            return idx;
        } catch (Exception e) {
            log.error("Failed to retrieve idx from Redis for redisKey: {}", redisKey, e);
            throw new RuntimeException("Error retrieving idx from Redis");
        }
    }

    public String getUsernameFromRedis(String memberIdx) {
        // Redis 키 생성 (e.g., "member:1")
        String redisKey = "member:" + memberIdx;

        try {
            // Redis 에서 "username" 필드 가져오기
            String username = (String) redisTemplate.opsForHash().get(redisKey, "username");
            log.info("Retrieved username: {} for key: {}", username, redisKey);
            return username;
        } catch (Exception e) {
            log.error("Failed to retrieve username from Redis for key: {}", redisKey, e);
            throw new RuntimeException("Error retrieving username from Redis");
        }
    }

    public String getUsernameByUserIdx(String userIdx) {
        return memberRepository.findByIdx(userIdx)
                .map(Member::getUsername) // Member 객체에서 username 가져오기
                .orElseThrow(() -> new RuntimeException("User not found for userIdx: " + userIdx));
    }

     */
}
