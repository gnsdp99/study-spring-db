package com.gnsdp99.jdbc.service;

import com.gnsdp99.jdbc.domain.Member;
import com.gnsdp99.jdbc.repository.MemberRepository;
import com.gnsdp99.jdbc.repository.MemberRepositoryV4_2;
import com.gnsdp99.jdbc.repository.MemberRepositoryV5;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JdbcTemplate 적용
 */
@Slf4j
@SpringBootTest
class MemberServiceV5Test {

    public static final String MEMBER_A = "MEMBER_A";
    public static final String MEMBER_B = "MEMBER_B";
    public static final String MEMBER_INVALID = "INVALID";

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberServiceV4_1 memberService;

    @RequiredArgsConstructor
    @TestConfiguration
    static class TestConfig {
        private final DataSource dataSource;

        @Bean
        MemberRepository memberRepository() {
            return new MemberRepositoryV5(new JdbcTemplate(dataSource));
        }

        @Bean
        MemberServiceV4_1 memberService() {
            return new MemberServiceV4_1(memberRepository());
        }
    }

    @AfterEach
    void after() {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_INVALID);
    }

    @Test
    void AOP_적용() {
        log.info("memberService class={}", memberService.getClass());
        log.info("memberRepository class={}", memberRepository.getClass());
        assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    void 이체_성공() {
        Member memberA = new Member(MEMBER_A, 18000);
        Member memberB = new Member(MEMBER_B, 12000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        log.info("START TX");
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 3000);
        log.info("END TX");

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(15000);
        assertThat(findMemberB.getMoney()).isEqualTo(15000);
    }

    @Test
    void 이체_실패() {
        Member memberA = new Member(MEMBER_A, 18000);
        Member memberInvalid = new Member(MEMBER_INVALID, 12000);
        memberRepository.save(memberA);
        memberRepository.save(memberInvalid);

        assertThrows(IllegalStateException.class,
                () -> memberService.accountTransfer(memberA.getMemberId(), memberInvalid.getMemberId(), 3000));

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberInvalid = memberRepository.findById(memberInvalid.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(18000);
        assertThat(findMemberInvalid.getMoney()).isEqualTo(12000);
    }
}