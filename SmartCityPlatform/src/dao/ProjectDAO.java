package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    // Açık Projeleri Getirme (findAllOpen)
    public List<String> findAllOpen() {
        List<String> projects = new ArrayList<>();
        String query = "SELECT title FROM Projects WHERE status = 'Açık'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                projects.add(rs.getString("title"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return projects;
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
    // Aktif proje ve bekleyen başvuru sayılarını döndürür
public void printDashboardSummary() {
    String query = "SELECT " +
                   "(SELECT COUNT(*) FROM Projects WHERE status = 'Açık') as active_projects, " +
                   "(SELECT COUNT(*) FROM Applications WHERE status = 'Beklemede') as pending_apps";

    try (Connection conn = util.DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        
        if (rs.next()) {
            System.out.println("\n--- YÖNETİCİ ÖZET PANELİ ---");
            System.out.println("Aktif Proje Sayısı: " + rs.getInt("active_projects"));
            System.out.println("Onay Bekleyen Başvuru: " + rs.getInt("pending_apps"));
        }
    } catch (SQLException e) { e.printStackTrace(); }
}
}