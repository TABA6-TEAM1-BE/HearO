package com.example.user_service.controller;

import com.example.user_service.dto.SignInDto;
import com.example.user_service.dto.SignUpDto;
import com.example.user_service.entity.Member;
import com.example.user_service.entity.RedisMember;
import com.example.user_service.login.CustomUserDetails;
import com.example.user_service.service.AuthService;
import com.example.user_service.service.MemberService;
import com.example.user_service.service.RedisMemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
//@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Mock
    MemberService memberService;

    @Mock
    RedisMemberService redisMemberService;

    @InjectMocks
    MemberController memberController;

    private MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup() throws Exception {
        //given
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setUsername("kjh");
        signUpDto.setPassword("1234");

        HashMap <String,String> response = new HashMap<>();
        response.put("message", "Sign up successful");
        ResponseEntity<Map<String,String>> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

        Mockito.when(memberService.signUp(any(String.class),any(String.class))).thenReturn(responseEntity);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/members/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto))
        );
        //then
        resultActions.andExpect(status().is2xxSuccessful());

    }

    @Test
    void signIn() throws Exception {
        //given
        SignInDto signInDto = new SignInDto();
        signInDto.setUsername("kjh");
        signInDto.setPassword("1234");
        signInDto.setFcmtoken("kjh");

        RedisMember redisMember = new RedisMember("asdfd-3123hu-321j3bj-13123","1","kjh","fcmToken");

        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        CustomUserDetails customUserDetails = new CustomUserDetails("1","kjh","1234", new ArrayList<>());
        Mockito.when(memberService.signIn("kjh","1234")).thenReturn(mockAuthentication);
//        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        Mockito.when(mockAuthentication.getPrincipal()).thenReturn(customUserDetails);
        Mockito.when(mockAuthentication.isAuthenticated()).thenReturn(true);
        Mockito.when(redisMemberService.save(any(RedisMember.class))).thenReturn(redisMember);



        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/members/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInDto))
        );
        //then
        resultActions.andExpect(status().is2xxSuccessful());
        resultActions.andExpect(jsonPath("redisKey").value("asdfd-3123hu-321j3bj-13123"));
    }

    @Test
    @DisplayName("get Auth 성공")
    void getAuthSuccess() throws Exception {
        //given
        RedisMember redisMember = new RedisMember("asdfd-3123hu-321j3bj-13123","1","kjh","fcmToken");
        Mockito.when(redisMemberService.findById(any(String.class))).thenReturn(Optional.of(redisMember));
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/members/auth/{id}",redisMember.getId())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().is2xxSuccessful());

    }

    @Test
    @DisplayName("get Auth 실패")
    void getAuthFail() throws Exception {
        //given
        Mockito.when(redisMemberService.findById(any(String.class))).thenReturn(Optional.empty());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/members/auth/{id}","afid-1h78-ds8afh9")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().is4xxClientError());

    }
}