package com.example.apigateway_service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 요청 경로 확인
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.equals("/members/sign-in")) {
            // ResponseBodyCaptureWrapper로 응답을 감싸서 사용
            ResponseBodyCaptureWrapper responseWrapper = new ResponseBodyCaptureWrapper(httpResponse);


            // 필터 체인 실행 (요청 처리)
            chain.doFilter(httpRequest, responseWrapper);

            // 응답 상태 코드 확인
            int statusCode = responseWrapper.getStatus();
            if (statusCode != HttpServletResponse.SC_OK) { // 로그인 실패 시
                log.error("Login failed with status code: {}", statusCode);
                String capturedResponseBody = responseWrapper.getCapturedResponseBody();
                log.info("Captured Response Body: {}", capturedResponseBody);

                httpResponse.resetBuffer();
                httpResponse.setStatus(statusCode);
                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                String jsonResponse = "{\"error\": \"" + capturedResponseBody + "\"}";
                httpResponse.getOutputStream().write(jsonResponse.getBytes());
                httpResponse.flushBuffer();
                return; // 에러 상태를 그대로 반환, 토큰 생성 방지
            }

            // 3. 응답 바디 가로채기 -> 로그인 성공 시
            String capturedResponseBody = responseWrapper.getCapturedResponseBody();
            log.info("Captured Response Body: {}", capturedResponseBody);

            // 4. redisKey 값 추출
            String redisKeyValue = extractRedisKeyValue(capturedResponseBody); // JSON에서 "af-314344df-ff-fdsfaf" 추출
            log.info("Extracted Redis Key Value: {}", redisKeyValue);

            // 5. JWT 토큰 생성
            JwtToken jwtToken = jwtTokenProvider.generateToken(redisKeyValue); // redisKeyValue를 사용해 토큰 생성
            log.info("Generated JWT Token: {}", jwtToken);

            // 6. 응답으로 JWT 토큰만 반환
            httpResponse.resetBuffer(); // 기존 응답 내용 제거
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            String jsonResponse = "{\"token\": \"" + jwtToken.getAccessToken() + "\"}";
            httpResponse.getOutputStream().write(jsonResponse.getBytes());
            httpResponse.flushBuffer(); // 최종적으로 클라이언트에게 응답
        } else {
            // 1. Request Header에서 JWT 토큰 추출
            String token = resolveToken(httpRequest);
            log.info("Extracted Token: {}", token);

            // 2. 토큰 유효성 검사 및 SecurityContext 설정
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // JWT 토큰 디코딩 및 idx(subject) 추출
                String idx = jwtTokenProvider.getSubject(token);
                log.info("Extracted idx from Token: {}", idx);

                // SecurityContext 설정
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 요청 헤더에 idx 추가
                HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public String getHeader(String name) {
                        if ("X-User-Idx".equalsIgnoreCase(name)) {
                            return idx; // 새로 추가된 헤더
                        }
                        return super.getHeader(name);
                    }

                    @Override
                    public Enumeration<String> getHeaders(String name) {
                        if ("X-User-Idx".equalsIgnoreCase(name)) {
                            return Collections.enumeration(Collections.singletonList(idx));
                        }
                        return super.getHeaders(name);
                    }

                    @Override
                    public Enumeration<String> getHeaderNames() {
                        List<String> headerNames = Collections.list(super.getHeaderNames());
                        headerNames.remove("Authorization");
                        headerNames.add("X-User-Idx");
                        return Collections.enumeration(headerNames);

                    }
                };

                // 필터 체인 실행
                chain.doFilter(wrappedRequest, httpResponse);
                log.info("After doFilter: {}", wrappedRequest.getHeader("X-User-Idx"));// 잘 되는거 확인함
            } else {
                // 필터 체인 실행
                chain.doFilter(httpRequest, httpResponse);
                return;
            }
        }
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String extractRedisKeyValue(String responseBody) {
        try {
            // JSON 파싱을 통해 "redisKey" 값 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String redisKey = jsonNode.get("redisKey").asText(); // 예: "member:1"
            return redisKey; // 숫자 부분만 반환 ("1")
        } catch (Exception e) {
            log.error("Failed to extract Redis Key Value from response body", e);
            return null;
        }
    }
}