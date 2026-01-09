package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Application;
import util.DBConnection;

public class ApplicationDAO {
    
    // Başvuru Kaydetme (save)
    public void save(int projectId, int userId, String notes) {
        String query = "INSERT INTO Applications (project_id, user_id, notes) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, notes);
            pstmt.executeUpdate();
            System.out.println("Başvurunuz alındı! 📩");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Projeye Göre Başvuruları Bulma (findByProjectId)
    public void findByProjectId(int projectId) {
        String query = "SELECT * FROM Applications WHERE project_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("Başvuru ID: " + rs.getInt("application_id") + " | Durum: " + rs.getString("status"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateStatus(int applicationId, String newStatus) {
    String query = "UPDATE Applications SET status = ? WHERE application_id = ?";
    try (Connection conn = util.DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, newStatus);
        pstmt.setInt(2, applicationId);
        pstmt.executeUpdate();
        System.out.println("Başvuru sonucu güncellendi: " + newStatus + " ✅");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
    /**
     * Projeye yapılan başvuruların detaylı listesini getirir (Stored Procedure kullanarak)
     * GetProjectApplications() stored procedure'ını kullanır
     * 
     * @param projectId Proje ID'si
     * @return List of application maps with details
     * @author Elif
     */
    public List<Map<String, Object>> getProjectApplications(int projectId) {
        List<Map<String, Object>> applications = new ArrayList<>();
        try (Connection conn = util.DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GetProjectApplications(?)}")) {
            
            cstmt.setInt(1, projectId);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> appData = new HashMap<>();
                appData.put("applicationId", rs.getInt("application_id"));
                appData.put("applicationDate", rs.getString("application_date"));
                appData.put("status", rs.getString("status"));
                appData.put("applicantName", rs.getString("basvuran_adi"));
                appData.put("applicantUsername", rs.getString("basvuran_kullanici_adi"));
                appData.put("notes", rs.getString("notes"));
                appData.put("projectTitle", rs.getString("proje_basligi"));
                applications.add(appData);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return applications;
    }
    
    /**
     * Eski metod uyumluluk için (deprecated - console'a yazdırır)
     * @deprecated Use getProjectApplications() instead
     */
    @Deprecated
    public void printProjectApplications(int projectId) {
        List<Map<String, Object>> applications = getProjectApplications(projectId);
        System.out.println("\n--- PROJE BAŞVURULARI DETAYLI LİSTE ---");
        System.out.println("Başvuru ID | Tarih | Durum | Başvuran | Notlar");
        System.out.println("-----------------------------------------------");
        
        if (applications.isEmpty()) {
            System.out.println("Bu projeye henüz başvuru yapılmamış.");
        } else {
            for (Map<String, Object> app : applications) {
                System.out.printf("%-10d | %s | %-10s | %-15s | %s\n", 
                    app.get("applicationId"), 
                    app.get("applicationDate"), 
                    app.get("status"), 
                    app.get("applicantName"), 
                    app.get("notes") != null ? app.get("notes") : "-");
            }
        }
    }
    // Kullanıcıya Ait Başvuruları Listeleme (findByUserId) - Liste ve proje başlığı döndürür
    public List<Map<String, Object>> findByUserIdWithProjectInfo(int userId) {
        List<Map<String, Object>> applications = new ArrayList<>();
        String query = """
            SELECT a.application_id, a.project_id, a.user_id, a.status, 
                   a.application_date, a.notes, p.title as project_title
            FROM Applications a
            JOIN Projects p ON a.project_id = p.project_id
            WHERE a.user_id = ?
            ORDER BY a.application_date DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> appData = new java.util.HashMap<>();
                appData.put("applicationId", rs.getInt("application_id"));
                appData.put("projectId", rs.getInt("project_id"));
                appData.put("userId", rs.getInt("user_id"));
                appData.put("status", rs.getString("status"));
                appData.put("notes", rs.getString("notes"));
                appData.put("projectTitle", rs.getString("project_title"));
                if (rs.getTimestamp("application_date") != null) {
                    appData.put("applicationDate", rs.getTimestamp("application_date").toString());
                }
                applications.add(appData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }
    
    // Eski metod uyumluluk için (Application nesneleri döndürür)
    public List<Application> findByUserId(int userId) {
        List<Application> applications = new ArrayList<>();
        String query = """
            SELECT a.application_id, a.project_id, a.user_id, a.status, 
                   a.application_date, a.notes
            FROM Applications a
            WHERE a.user_id = ?
            ORDER BY a.application_date DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Application app = new Application();
                app.setApplicationId(rs.getInt("application_id"));
                app.setProjectId(rs.getInt("project_id"));
                app.setUserId(rs.getInt("user_id"));
                app.setStatus(rs.getString("status"));
                app.setNotes(rs.getString("notes"));
                if (rs.getTimestamp("application_date") != null) {
                    app.setApplicationDate(rs.getTimestamp("application_date"));
                }
                applications.add(app);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }
}