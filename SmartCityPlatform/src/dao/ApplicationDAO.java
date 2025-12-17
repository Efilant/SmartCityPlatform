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
}