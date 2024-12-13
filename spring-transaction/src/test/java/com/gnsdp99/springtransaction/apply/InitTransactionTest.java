package com.gnsdp99.springtransaction.apply;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InitTransactionTest {

    @Test
    void init() {

    }

    @TestConfiguration
    static class initTransactionTestConfig {

        @Bean
        Hello hello() {
            return new Hello();
        }
    }

    static class Hello {

        // @PostConstruct 메서드는 스프링 컨테이너 초기화 시점에 자동으로 호출된다.
        // 초기화 이후 트랜잭션 AOP가 적용되기 때문에 @Transaction이 동작하지 않는다.
        @PostConstruct
        @Transactional
        public void initV1() {
            boolean transactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct transaction active={}", transactionActive);
        }

        // @EventListener(ApplicationReadyEvent.class) 메서드는 스프링 컨테이너가 완전히 생성된 후 자동으로 호출된다.
        // 트랜잭션 AOP가 적용된 후이기 때문에 @Transactional이 동작한다.
        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void initV2() {
            boolean transactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init ApplicationReadyEvent transaction active={}", transactionActive);
        }
    }
}
