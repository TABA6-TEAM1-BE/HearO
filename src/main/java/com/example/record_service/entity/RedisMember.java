package com.example.record_service.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RedisMember {

    private String id;

    private String idx; //사용자 idx

    private String username; // 사용자 이름

    public RedisMember(String idx, String username) {
        this.idx = idx;
        this.username = username;
    }
}
