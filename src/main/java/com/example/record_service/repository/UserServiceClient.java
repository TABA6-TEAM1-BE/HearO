package com.example.record_service.repository;

import com.example.record_service.entity.RedisMember;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/members/auth/{id}")
    ResponseEntity<RedisMember> getMemberById(@PathVariable("id") String id);

    @GetMapping("/members/auth/idx/{userIdx}")
    ResponseEntity<RedisMember> getMemberByUserIdx(@PathVariable("userIdx") String userIdx);
}
