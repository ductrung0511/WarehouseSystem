package com.warehouse;
        import java.sql.*;
import java.sql.SQLException;
public class DatabaseConfig{
    private static final String URL = "jdbc:mysql://localhost:3306/mysql?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String USERNAME = "root";
    private static final String PASS = "Ductrung!23";
    
    public static Connection getConnection () throws SQLException {
        try{
         Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASS);
        } 
        catch (ClassNotFoundException e){
            throw new SQLException("MySQL jdbc not found", e);
        }
            }

    }