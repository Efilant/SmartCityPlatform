package dao;

import java.sql.*;
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
     * @author Elif
     */
    public void printProjectApplications(int projectId) {
        try (Connection conn = util.DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GetProjectApplications(?)}")) {
            
            cstmt.setInt(1, projectId);
            ResultSet rs = cstmt.executeQuery();
            
            System.out.println("\n--- PROJE BAŞVURULARI DETAYLI LİSTE ---");
            System.out.println("Başvuru ID | Tarih | Durum | Başvuran | Notlar");
            System.out.println("-----------------------------------------------");
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                int appId = rs.getInt("application_id");
                String tarih = rs.getString("application_date");
                String durum = rs.getString("status");
                String basvuran = rs.getString("basvuran_adi");
                String notlar = rs.getString("notes");
                
                System.out.printf("%-10d | %s | %-10s | %-15s | %s\n", 
                    appId, tarih, durum, basvuran, notlar != null ? notlar : "-");
            }
            
            if (!hasData) {
                System.out.println("Bu projeye henüz başvuru yapılmamış.");
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }
}