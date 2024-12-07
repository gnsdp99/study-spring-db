package com.gnsdp99.jdbc.service;

import com.gnsdp99.jdbc.domain.Member;
import com.gnsdp99.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 사용 - 파라미터 연동
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection conn = dataSource.getConnection();
        try {
            // 트랜잭션 시작
            conn.setAutoCommit(false);
            bizLogic(conn, fromId, toId, money);
            // 성공 시 커밋
            conn.commit();
        } catch (Exception e) {
            // 실패 시 롤백
            conn.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(conn);
        }
    }

    private void bizLogic(Connection conn, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(conn, fromId);
        Member toMember = memberRepository.findById(conn, toId);

        memberRepository.update(conn, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(conn, toId, toMember.getMoney() + money);
    }

    private void release(Connection conn) {
        if (conn != null) {
            try {
                // 커넥션 풀 고려 - 커넥션의 auto commit이 false로 유지되면 안됨
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                log.error("close error ", e);
            }
        }
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("INVALID")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
