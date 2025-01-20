package com.example.user_service.service;

import com.example.user_service.entity.RedisMember;
import com.example.user_service.repository.RedisMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisMemberService {
    private final RedisMemberRepository redisMemberRepository;

    public RedisMember save(RedisMember redisMember) {
        RedisMember savedRedisMember = redisMemberRepository.save(redisMember);
        return savedRedisMember;
    }
    public Optional<RedisMember> findById(String id){
        return redisMemberRepository.findById(id);
    }

    public Optional<RedisMember> findByIdx(String idx){
        return redisMemberRepository.findByIdx(idx);
    }

}
