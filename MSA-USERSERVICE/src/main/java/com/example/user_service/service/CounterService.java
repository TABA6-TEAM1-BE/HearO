package com.example.user_service.service;

import com.example.user_service.entity.UserIdx;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CounterService {
    private final MongoOperations mongoOperations;

    public long getNextUserIdxSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1); // seq 값을 1 증가
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);

        UserIdx counter = mongoOperations.findAndModify(query, update, options, UserIdx.class);
        return counter != null ? counter.getSeq() : 1; // seq 값 반환
    }

}
