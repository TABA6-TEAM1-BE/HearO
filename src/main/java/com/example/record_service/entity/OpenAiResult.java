package com.example.record_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "GPT_Response")
public class OpenAiResponseDocument {

    @Id
    private String id;
    private String prompt;
    private String response;

}
