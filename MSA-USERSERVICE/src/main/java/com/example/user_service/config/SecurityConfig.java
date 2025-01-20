package com.example.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.httpBasic((hp)->hp.disable())
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .csrf((cs)->cs.disable())
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement((sm)->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests((ahr)->ahr.requestMatchers("/members/**", "/auth/**").permitAll())
                .build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity.httpBasic((hp)->hp.disable())
//                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
//                .csrf((cs)->cs.disable())
//                // JWT를 사용하기 때문에 세션을 사용하지 않음
//                .sessionManagement((sm)->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests((ahr)->ahr.requestMatchers("/members/sign-in","/members/sign-up","/members/find", "/s3/upload").permitAll()
//                        .requestMatchers("/members/test").hasRole("USER").anyRequest().authenticated())
//                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class).build();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

