package com.example.user_service.login;

import com.example.user_service.entity.Member;
import com.example.user_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인 시에 DB 에서 유저 정보와 권한 정보를 가져와서 해당 정보를 기반으로 CustomUserDetails 객체를 생성해 리턴
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    // DB 에 있는 사용자의 아이디와 비밀번호를 담고 있는 CustomUserDetails 생성
    // -> authenticationManager 가 이 객체와 UsernamePasswordAuthenticationToken 과 비교하여 사용자 인증을 해줌
    private UserDetails createUserDetails(Member member) {
        return CustomUserDetails.builder()
                .idx(member.getIdx())
                .username(member.getUsername())
                .password(passwordEncoder.encode(member.getPassword()))
                .roles(Arrays.asList(member.getRoles().toArray(new String[0]))) // 배열을 리스트로 변환
                .build();
    }

}
