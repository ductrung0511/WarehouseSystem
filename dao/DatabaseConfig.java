package com.warehouse.dao;

import  java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

        


public class DatabaseConfig {
    private static final String url = "jdbc:mysql://localhost:3306/warehouse?useSSL=false&serverTimezone=UTC";
    private static final String usr = "root";
   private static final  String pass = "Ductrung!23";
   
   // static so it remain for the duration of sql execution...
   public static Connection getConnection() throws SQLException {
       try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // why this line ? 
            // ==> inititate the block below:
//            static {
//    DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
//}

// load the driver before driverManager connect to mySQL

            return DriverManager.getConnection(url , usr, pass);
       }
       catch (ClassNotFoundException e){
            throw new SQLException(e);}
   }
}