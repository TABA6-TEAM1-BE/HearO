package com.example.user_service.repository;

import com.example.user_service.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
//@ExtendWith(SpringExtension.class)
class MemberRepositoryTest {

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 저장 확인")
    void findByUsername() {
        //given
        Member member = new Member();
        member.setUsername("user1");
        member.setPassword("1234");
        member.setIdx("11"); // idx를 문자열로 변환하여 설정
        ArrayList<String> roles = new ArrayList<>();
        roles.add("USER");
        member.setRoles(roles);
        memberRepository.save(member);

        //when
        Optional<Member> user = memberRepository.findByUsername("user1");
        Optional<Member> notuser = memberRepository.findByUsername("babo");

        //then
        assertTrue(user.isPresent());
        assertEquals("user1", user.get().getUsername());
        assertEquals(user.get().getPassword(), "1234");
        assertThrows(NoSuchElementException.class, () -> notuser.get().getUsername());
    }

    @Test
    void findByIdx() {
        //given
        Member member = new Member();
        member.setUsername("user1");
        member.setPassword("1234");
        member.setIdx("11"); // idx를 문자열로 변환하여 설정
        ArrayList<String> roles = new ArrayList<>();
        roles.add("USER");
        member.setRoles(roles);
        memberRepository.save(member);

        //when
        Optional<Member> user = memberRepository.findByIdx("11");
        Optional<Member> non = memberRepository.findByIdx("12");

        //then
        assertTrue(user.isPresent());
        assertEquals("user1", user.get().getUsername());
        assertEquals(user.get().getPassword(), "1234");
        assertThrows(NoSuchElementException.class, () -> non.get().getUsername());
    }
}