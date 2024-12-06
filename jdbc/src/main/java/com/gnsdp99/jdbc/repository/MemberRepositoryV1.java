package com.gnsdp99.jdbc.repository;

import com.gnsdp99.jdbc.connection.DBConnectionUtil;
import com.gnsdp99.jdbc.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * JDBC - DataSource 사용
 */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public Member save(Member member) throws SQLException {
        String sql = """
                insert into member(member_id, money)
                values (?, ?)
                """;

        // SQL 작업에 실패해도 리소스를 반드시 해제해야 함.
        try (
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            // 파라미터 바인딩
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            // 쓰기 작업 실행
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error ", e);
            throw e;
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = """
                select * from member
                where member_id = ?
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            pstmt.setString(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                // 읽기 작업 실행
                if (rs.next()) {
                    // ResultSet에 데이터가 존재함
                    return new Member(
                            rs.getString("member_id"),
                            rs.getInt("money")
                    );
                } else {
                    throw new NoSuchElementException("member not found memberId=" + memberId);
                }
            } catch (SQLException e) {
                log.error("db error ", e);
                throw e;
            }
        } catch (SQLException e) {
            log.error("db error ", e);
            throw e;
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = """
                update member
                set money = ?
                where member_id = ?
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error ", e);
            throw e;
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = """
                delete from member
                where member_id = ?
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error ", e);
            throw e;
        }
    }

    private Connection getConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        logConeection(conn);
        return conn;
    }

    private void logConeection(Connection conn) {
        log.info("connection={}, class={}", conn, conn.getClass());
    }
}
