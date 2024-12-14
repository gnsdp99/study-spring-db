package com.gnsdp99.springtransaction.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    @Transactional
    public void joinV1(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("memberRepository 트랜잭션 시작");
        memberRepository.save(member);
        log.info("memberRepository 트랜잭션 종료");

        log.info("logRepository 트랜잭션 시작");
        logRepository.save(logMessage);
        log.info("logRepository 트랜잭션 종료");
    }

    @Transactional
    public void joinV2(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("memberRepository 트랜잭션 시작");
        memberRepository.save(member);
        log.info("memberRepository 트랜잭션 종료");

        log.info("logRepository 트랜잭션 시작");
        try {
            logRepository.save(logMessage);
        } catch (RuntimeException e) {
            log.error("로그 저장 실패. logMessage={}", logMessage.getMessage());
        }
        log.info("logRepository 트랜잭션 종료");
    }
}
