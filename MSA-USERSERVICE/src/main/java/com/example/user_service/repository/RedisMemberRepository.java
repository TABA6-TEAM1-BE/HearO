package com.example.user_service.repository;

import com.example.user_service.entity.RedisMember;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@RedisHash
public interface RedisMemberRepository extends CrudRepository<RedisMember, String> {
    public Optional<RedisMember> findById(String id);
    public Optional<RedisMember> findByIdx(String idx);

}
