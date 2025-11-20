package com.example.cvbuilder;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    private static final String URL = "jdbc:sqlite:cv.db";
    public static Connection connect() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("✔ Connected to SQLite database");
        } catch (Exception e) {
            System.out.println("❌ Database connection failed");
            e.printStackTrace();
        }

        return conn;
    }
}
