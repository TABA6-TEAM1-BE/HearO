package com.example.record_service.repository;

import com.example.record_service.entity.OpenAiResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenAiResponseRepository extends MongoRepository<OpenAiResult, String> {
}
