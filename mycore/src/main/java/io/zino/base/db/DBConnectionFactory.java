package io.zino.base.db;

import java.sql.*;

public class DBConnectionFactory {
    private Connection conn;
    private static DBConnectionFactory instance =
            new DBConnectionFactory(
                    "jdbc:postgresql://localhost:5432/CORE",
                    "core",
                    "core");

    private String url;
    private String user;
    private String password;

    private DBConnectionFactory(String url, String user,String password){
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DBConnectionFactory getInstance() {
        return instance;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
