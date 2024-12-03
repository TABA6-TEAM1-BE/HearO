package com.example.record_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "recordIdx")
public class RecordIdx {
    @Id
    private String id;
    private long seq;  // 증가되는 시퀀스 값
}
