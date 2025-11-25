package com.example.cvbuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

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

    public static void init() throws SQLException {
        try (Connection conn = connect()) {
            if (conn == null) throw new SQLException("Unable to obtain database connection");
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");

                conn.setAutoCommit(false);

                stmt.addBatch("CREATE TABLE IF NOT EXISTS cvs ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "full_name TEXT,"
                        + "email TEXT,"
                        + "phone TEXT,"
                        + "address TEXT,"
                        + "profile_image_path TEXT,"
                        + "skills TEXT,"
                        + "created_at TEXT DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now')) ,"
                        + "updated_at TEXT"
                        + ");");

                stmt.addBatch("CREATE TABLE IF NOT EXISTS education ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "cv_id INTEGER NOT NULL,"
                        + "seq INTEGER DEFAULT 0,"
                        + "school TEXT,"
                        + "degree TEXT,"
                        + "result TEXT,"
                        + "FOREIGN KEY (cv_id) REFERENCES cvs(id) ON DELETE CASCADE"
                        + ");");

                stmt.addBatch("CREATE TABLE IF NOT EXISTS experience ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "cv_id INTEGER NOT NULL,"
                        + "seq INTEGER DEFAULT 0,"
                        + "job_title TEXT,"
                        + "company TEXT,"
                        + "start_date TEXT,"
                        + "end_date TEXT,"
                        + "currently_working INTEGER DEFAULT 0,"
                        + "FOREIGN KEY (cv_id) REFERENCES cvs(id) ON DELETE CASCADE"
                        + ");");

                stmt.addBatch("CREATE TABLE IF NOT EXISTS project ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "cv_id INTEGER NOT NULL,"
                        + "seq INTEGER DEFAULT 0,"
                        + "title TEXT,"
                        + "description TEXT,"
                        + "link TEXT,"
                        + "FOREIGN KEY (cv_id) REFERENCES cvs(id) ON DELETE CASCADE"
                        + ");");
                stmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
                throw e;
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }
}
