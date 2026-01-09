package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Project;
import util.DBConnection;

public class ProjectDAO {
    
    // Proje Kaydetme (save)
    public void save(String title, String desc, String start, String end) {
        String query = "INSERT INTO Projects (title, description, start_date, end_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.setString(2, desc);
            pstmt.setString(3, start);
            pstmt.setString(4, end);
            pstmt.executeUpdate();
            System.out.println("Proje başarıyla kaydedildi! ✅");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Tüm Projeleri Getirme (findAll) - Project nesneleri döndürür
    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT project_id, title, description, start_date, end_date, status FROM Projects ORDER BY project_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Project project = new Project();
                project.setProjectId(rs.getInt("project_id"));
                project.setTitle(rs.getString("title"));
                project.setDescription(rs.getString("description"));
                project.setStatus(rs.getString("status"));
                if (rs.getDate("start_date") != null) {
                    project.setStartDate(rs.getDate("start_date"));
                }
                if (rs.getDate("end_date") != null) {
                    project.setEndDate(rs.getDate("end_date"));
                }
                projects.add(project);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return projects;
    }
    
    // Açık Projeleri Getirme (findAllOpen) - Project nesneleri döndürür
    public List<Project> findAllOpen() {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT project_id, title, description, start_date, end_date, status FROM Projects WHERE status = 'Açık'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Project project = new Project();
                project.setProjectId(rs.getInt("project_id"));
                project.setTitle(rs.getString("title"));
                project.setDescription(rs.getString("description"));
                project.setStatus(rs.getString("status"));
                if (rs.getDate("start_date") != null) {
                    project.setStartDate(rs.getDate("start_date"));
                }
                if (rs.getDate("end_date") != null) {
                    project.setEndDate(rs.getDate("end_date"));
                }
                projects.add(project);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return projects;
    }
    
    // Eski metod uyumluluk için (String listesi döndürür)
    public List<String> findAllOpenTitles() {
        List<String> titles = new ArrayList<>();
        String query = "SELECT title FROM Projects WHERE status = 'Açık'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                titles.add(rs.getString("title"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return titles;
    }

    // Durum Güncelleme (updateStatus)
    public void updateStatus(int projectId, String newStatus) {
    String query = "UPDATE Projects SET status = ? WHERE project_id = ?";
    try (Connection conn = util.DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, newStatus);
        pstmt.setInt(2, projectId);
        pstmt.executeUpdate();
        System.out.println("Proje durumu '" + newStatus + "' olarak güncellendi! 🔄");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    // Proje Güncelleme (update)
    public void update(int projectId, String title, String description, String startDate, String endDate) {
        String query = "UPDATE Projects SET title = ?, description = ?, start_date = ?, end_date = ? WHERE project_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);
            pstmt.setInt(5, projectId);
            pstmt.executeUpdate();
            System.out.println("Proje başarıyla güncellendi! ✅");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Proje güncellenirken bir hata oluştu: " + e.getMessage());
        }
    }

    // Proje Silme (delete)
    public void delete(int projectId) {
        String query = "DELETE FROM Projects WHERE project_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, projectId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Proje başarıyla silindi! 🗑️");
            } else {
                throw new RuntimeException("Proje bulunamadı!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Proje silinirken bir hata oluştu: " + e.getMessage());
        }
    }
    /**
     * Aktif proje ve bekleyen başvuru sayılarını döndürür (Stored Procedure kullanarak)
     * GetSystemStats() stored procedure'ını kullanır
     * 
     * @return Map containing dashboard statistics
     * @author Elif
     */
    public java.util.Map<String, Object> getDashboardSummary() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        try (Connection conn = util.DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GetSystemStats()}")) {
            
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                stats.put("totalIssues", rs.getInt("total_issues"));
                stats.put("activeProjects", rs.getInt("active_projects"));
                stats.put("totalApplications", rs.getInt("total_applications"));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return stats;
    }
    
    /**
     * Eski metod uyumluluk için (deprecated - console'a yazdırır)
     * @deprecated Use getDashboardSummary() instead
     */
    @Deprecated
    public void printDashboardSummary() {
        java.util.Map<String, Object> stats = getDashboardSummary();
        System.out.println("\n--- YÖNETİCİ ÖZET PANELİ ---");
        System.out.println("Toplam Şikayet Sayısı: " + stats.get("totalIssues"));
        System.out.println("Aktif Proje Sayısı: " + stats.get("activeProjects"));
        System.out.println("Toplam Başvuru Sayısı: " + stats.get("totalApplications"));
    }
}