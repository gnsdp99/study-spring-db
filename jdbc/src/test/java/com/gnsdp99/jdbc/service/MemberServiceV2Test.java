package com.gnsdp99.jdbc.service;

import com.gnsdp99.jdbc.domain.Member;
import com.gnsdp99.jdbc.repository.MemberRepositoryV2;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static com.gnsdp99.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class MemberServiceV2Test {

    public static final String MEMBER_A = "MEMBER_A";
    public static final String MEMBER_B = "MEMBER_B";
    public static final String MEMBER_INVALID = "INVALID";

    MemberRepositoryV2 memberRepository;
    MemberServiceV2 memberService;

    @BeforeEach
    void before() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        memberRepository = new MemberRepositoryV2(dataSource);
        memberService = new MemberServiceV2(dataSource, memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_INVALID);
    }

    @Test
    void 이체_성공() throws SQLException {
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
    void 이체_실패() throws SQLException {
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