package com.ssafy.trip.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private final String driverClassName = "com.mysql.cj.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost:3306/ssafytrip?serverTimezone=UTC";
    private final String user = "ssafy";
    private final String password = "ssafy";
    private static DBUtil util = new DBUtil();

    public static DBUtil getUtil() {
        return util;
    }

    private DBUtil() {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        props.setProperty("profileSQL", "true");  // 쿼리 프로파일링 활성화

        return DriverManager.getConnection(url, props);
    }

    public void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignore) {

                }
            }
        }
    }
}
