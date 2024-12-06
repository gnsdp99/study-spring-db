package com.gnsdp99.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.gnsdp99.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() {
        try (
                Connection conn1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                Connection conn2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        ) {
            logConeection(conn1);
            logConeection(conn2);
        } catch (SQLException e) {
            log.error("db error ", e);
        }
    }

    /*
    설정과 사용의 분리
     */
    @Test
    void dataSourceDriverManager() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        // 커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10); // default = 10
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        // 커넥션 풀에 커넥션을 채우는 작업은 별도의 스레드에서 실행되기 때문에 이를 확인하려면 sleep이 필요함.
        Thread.sleep(1000);
    }

    private void useDataSource(DataSource dataSource) {
        try (
                Connection conn1 = dataSource.getConnection();
                Connection conn2 = dataSource.getConnection();
        ) {
            logConeection(conn1);
            logConeection(conn2);
        } catch (SQLException e) {
            log.error("db error ", e);
        }
    }

    private void logConeection(Connection conn) {
        log.info("connection={}, class={}", conn, conn.getClass());
    }
}
