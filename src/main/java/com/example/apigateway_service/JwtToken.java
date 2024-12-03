package com.example.apigateway_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JwtToken {
    private String grantType; //Bearer
    private String accessToken;
    private String refreshToken;
}
