package com.gnsdp99.springtransaction.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService rollbackService;

    @Test
    void runtimeException() {
        assertThrows(RuntimeException.class, () -> rollbackService.runTimeException());
    }

    @Test
    void checkedException() {
        assertThrows(Exception.class, () -> rollbackService.checkedException());
    }

    @Test
    void checkedExceptionRollbackFor() {
        assertThrows(Exception.class, () -> rollbackService.checkedExceptionRollbackFor());
    }

    @TestConfiguration
    static class rollbackTestConfig {

        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    static class RollbackService {

        // unchecked exception - 롤백
        @Transactional
        public void runTimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        // checked exception - 커밋
        @Transactional
        public void checkedException() throws Exception {
            log.info("call checkedException");
            throw new Exception();
        }

        // checked exception with rollbackFor - 롤백
        @Transactional(
                rollbackFor = Exception.class
        )
        public void checkedExceptionRollbackFor() throws Exception {
            log.info("call checkedExceptionRollbackFor");
            throw new Exception();
        }
    }
}
