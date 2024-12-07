package com.gnsdp99.jdbc.service;

import com.gnsdp99.jdbc.domain.Member;
import com.gnsdp99.jdbc.repository.MemberRepositoryV1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static com.gnsdp99.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceV1Test {

    public static final String MEMBER_A = "MEMBER_A";
    public static final String MEMBER_B = "MEMBER_B";
    public static final String MEMBER_INVALID = "INVALID";

    MemberRepositoryV1 memberRepository;
    MemberServiceV1 memberService;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV1(dataSource);
        memberService = new MemberServiceV1(memberRepository);
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

        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 3000);

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

        assertThat(findMemberA.getMoney()).isEqualTo(15000);
        assertThat(findMemberInvalid.getMoney()).isEqualTo(12000);
    }
}