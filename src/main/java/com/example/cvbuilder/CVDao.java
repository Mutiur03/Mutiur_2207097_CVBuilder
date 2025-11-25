package com.example.cvbuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CVDao {
    public long createCV(CVData cv) throws SQLException {
        String insertCvSql = "INSERT INTO cvs(full_name,email,phone,address,profile_image_path, skills) VALUES(?,?,?,?,?,?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(insertCvSql, Statement.RETURN_GENERATED_KEYS)) {
            if (conn == null) throw new SQLException("DB connection is null");
            conn.setAutoCommit(false);
            try {
                pstmt.setString(1, cv.getFullName());
                pstmt.setString(2, cv.getEmail());
                pstmt.setString(3, cv.getPhone());
                pstmt.setString(4, cv.getAddress());
                pstmt.setString(5, cv.getProfileImagePath());
                pstmt.setString(6, cv.getSkills());
                pstmt.executeUpdate();
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        long cvId = keys.getLong(1);
                        insertEducations(conn, cvId, cv.getEducationList());
                        insertExperiences(conn, cvId, cv.getExperienceList());
                        insertProjects(conn, cvId, cv.getProjectList());
                        conn.commit();
                        return cvId;
                    } else {
                        throw new SQLException("Failed to obtain generated key for CV");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }

    public Optional<CVData> getCV(long id) throws SQLException {
        String selectCv = "SELECT id,full_name,email,phone,address,profile_image_path,skills FROM cvs WHERE id = ?";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(selectCv)) {
            if (conn == null) throw new SQLException("DB connection is null");
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                CVData cv = new CVData();
                cv.setId(rs.getLong("id"));
                cv.setFullName(rs.getString("full_name"));
                cv.setEmail(rs.getString("email"));
                cv.setPhone(rs.getString("phone"));
                cv.setAddress(rs.getString("address"));
                cv.setProfileImagePath(rs.getString("profile_image_path"));
                cv.setSkills(rs.getString("skills"));
                cv.getEducationList().clear();
                cv.getExperienceList().clear();
                cv.getProjectList().clear();

                loadEducations(conn, id, cv.getEducationList());
                loadExperiences(conn, id, cv.getExperienceList());
                loadProjects(conn, id, cv.getProjectList());

                return Optional.of(cv);
            }
        }
    }

    public List<CVSummary> listCVSummaries() throws SQLException {
        String sql = "SELECT id, full_name, profile_image_path, created_at FROM cvs ORDER BY full_name";
        List<CVSummary> out = new ArrayList<>();
        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CVSummary s = new CVSummary(rs.getLong("id"), rs.getString("full_name"),  rs.getString("profile_image_path"));
                out.add(s);
            }
        }
        return out;
    }

    public boolean updateCV(CVData cv) throws SQLException {
        if (cv.getId() == null) throw new IllegalArgumentException("CV id is required for update");
        String updateSql = "UPDATE cvs SET full_name=?,email=?,phone=?,address=?,skills=?,profile_image_path=?,updated_at=strftime('%Y-%m-%dT%H:%M:%fZ','now') WHERE id = ?";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            if (conn == null) throw new SQLException("DB connection is null");
            conn.setAutoCommit(false);
            try {
                pstmt.setString(1, cv.getFullName());
                pstmt.setString(2, cv.getEmail());
                pstmt.setString(3, cv.getPhone());
                pstmt.setString(4, cv.getAddress());
                pstmt.setString(5, cv.getSkills());
                pstmt.setString(6, cv.getProfileImagePath());
                pstmt.setLong(7, cv.getId());
                int updated = pstmt.executeUpdate();

                deleteChildRows(conn, cv.getId());
                insertEducations(conn, cv.getId(), cv.getEducationList());
                insertExperiences(conn, cv.getId(), cv.getExperienceList());
                insertProjects(conn, cv.getId(), cv.getProjectList());
                conn.commit();
                return updated > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }

    public boolean deleteCV(long id) throws SQLException {
        String sql = "DELETE FROM cvs WHERE id = ?";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) throw new SQLException("DB connection is null");
            pstmt.setLong(1, id);
            int deleted = pstmt.executeUpdate();
            return deleted > 0;
        }
    }

    private void insertEducations(Connection conn, long cvId, List<CVData.Education> list) throws SQLException {
        if (list == null || list.isEmpty()) return;
        String sql = "INSERT INTO education(cv_id,seq,school,degree,result) VALUES(?,?,?,?,?)";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            int seq = 0;
            for (CVData.Education e : list) {
                p.setLong(1, cvId);
                p.setInt(2, seq++);
                p.setString(3, e.getSchool());
                p.setString(4, e.getDegree());
                p.setString(5, e.getResult());
                p.addBatch();
            }
            p.executeBatch();
        }
    }

    private void insertExperiences(Connection conn, long cvId, List<CVData.Experience> list) throws SQLException {
        if (list == null || list.isEmpty()) return;
        String sql = "INSERT INTO experience(cv_id,seq,job_title,company,start_date,end_date,currently_working) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            int seq = 0;
            for (CVData.Experience e : list) {
                p.setLong(1, cvId);
                p.setInt(2, seq++);
                p.setString(3, e.getJobTitle());
                p.setString(4, e.getCompany());
                p.setString(5, e.getStartDate());
                p.setString(6, e.getEndDate());
                p.setInt(7, e.isCurrentlyWorking() ? 1 : 0);
                p.addBatch();
            }
            p.executeBatch();
        }
    }

    private void insertProjects(Connection conn, long cvId, List<CVData.Project> list) throws SQLException {
        if (list == null || list.isEmpty()) return;
        String sql = "INSERT INTO project(cv_id,seq,title,description,link) VALUES(?,?,?,?,?)";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            int seq = 0;
            for (CVData.Project e : list) {
                p.setLong(1, cvId);
                p.setInt(2, seq++);
                p.setString(3, e.getTitle());
                p.setString(4, e.getDescription());
                p.setString(5, e.getLink());
                p.addBatch();
            }
            p.executeBatch();
        }
    }

    private void deleteChildRows(Connection conn, long cvId) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("DELETE FROM education WHERE cv_id = ?")) {
            p.setLong(1, cvId);
            p.executeUpdate();
        }
        try (PreparedStatement p = conn.prepareStatement("DELETE FROM experience WHERE cv_id = ?")) {
            p.setLong(1, cvId);
            p.executeUpdate();
        }
        try (PreparedStatement p = conn.prepareStatement("DELETE FROM project WHERE cv_id = ?")) {
            p.setLong(1, cvId);
            p.executeUpdate();
        }
    }

    private void loadEducations(Connection conn, long cvId, List<CVData.Education> out) throws SQLException {
        String sql = "SELECT school,degree,result FROM education WHERE cv_id = ? ORDER BY seq";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setLong(1, cvId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    out.add(new CVData.Education(rs.getString("school"), rs.getString("degree"), rs.getString("result")));
                }
            }
        }
    }

    private void loadExperiences(Connection conn, long cvId, List<CVData.Experience> out) throws SQLException {
        String sql = "SELECT job_title,company,start_date,end_date,currently_working FROM experience WHERE cv_id = ?";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setLong(1, cvId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    out.add(new CVData.Experience(rs.getString("job_title"), rs.getString("company"), rs.getString("start_date"), rs.getString("end_date"), rs.getInt("currently_working")==1));
                }
            }
        }
    }

    private void loadProjects(Connection conn, long cvId, List<CVData.Project> out) throws SQLException {
        String sql = "SELECT title,description,link FROM project WHERE cv_id = ? ORDER BY seq";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setLong(1, cvId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    out.add(new CVData.Project(rs.getString("title"), rs.getString("description"), rs.getString("link")));
                }
            }
        }
    }

    public static class CVSummary {
        private final long id;
        private final String fullName;
        private final String profileImagePath;

        public CVSummary(long id, String fullName, String profileImagePath) {
            this.id = id;
            this.fullName = fullName;
            this.profileImagePath = profileImagePath;}

        public long getId() { return id; }
        public String getFullName() { return fullName; }
        public String getProfileImagePath() { return profileImagePath; }
    }
}
