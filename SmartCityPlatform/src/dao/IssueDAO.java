package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Issue;
import util.DBConnection;

public class IssueDAO {
    
        
    public void createIssue(Issue issue) {
        String query;
        if (issue.getCategoryId() != null) {
            query = "INSERT INTO Issues (user_id, category_id, title, description, status) VALUES (?, ?, ?, ?, 'Yeni')";
        } else {
            query = "INSERT INTO Issues (user_id, title, description, status) VALUES (?, ?, ?, 'Yeni')";
        }

    try (Connection conn = util.DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            pstmt.setInt(paramIndex++, issue.getUserId());
            if (issue.getCategoryId() != null) {
                pstmt.setInt(paramIndex++, issue.getCategoryId());
            }
            pstmt.setString(paramIndex++, issue.getTitle());
            pstmt.setString(paramIndex++, issue.getDescription());

        pstmt.executeUpdate();
        System.out.println("Şikayetiniz başarıyla sisteme iletildi! ✅");

    } catch (SQLException e) {
        System.out.println("Şikayet oluşturulurken bir hata oluştu: " + e.getMessage());
            throw new RuntimeException("Şikayet oluşturulamadı: " + e.getMessage(), e);
    }
}
    public List<Issue> getAllIssues() {
    List<Issue> issues = new ArrayList<>();
    String query = "SELECT * FROM Issues";

    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            Issue issue = new Issue();
            issue.setIssueId(rs.getInt("issue_id"));
            issue.setUserId(rs.getInt("user_id"));
            if (rs.getObject("category_id") != null) {
                issue.setCategoryId(rs.getInt("category_id"));
            }
            issue.setTitle(rs.getString("title"));
            issue.setDescription(rs.getString("description"));
            issue.setStatus(rs.getString("status"));
            if (rs.getObject("priority") != null) {
                issue.setPriority(rs.getString("priority"));
            }
            if (rs.getTimestamp("created_at") != null) {
                issue.setCreatedAt(rs.getTimestamp("created_at").toString());
            }
            issues.add(issue);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return issues;
}
    public void updateIssueStatus(int issueId, String newStatus) {
        String query = "UPDATE Issues SET status = ? WHERE issue_id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, issueId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Şikayet durumu başarıyla güncellendi! ✅");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
}
    public List<Issue> findAllByUserId(int userId) {
    List<Issue> issues = new ArrayList<>();
    String query = "SELECT * FROM Issues WHERE user_id = ?";
    try (Connection conn = util.DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Issue issue = new Issue();
            issue.setIssueId(rs.getInt("issue_id"));
            issue.setTitle(rs.getString("title"));
            issue.setStatus(rs.getString("status"));
            issues.add(issue);
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return issues;
    
}
    /**
     * Kategori başarı oranlarını gösterir (Stored Procedure kullanarak)
     * GetCategorySuccessRate() stored procedure'ını kullanır
     * 
     * @author Elif
     */
    public void printCategoryReport() {
        try (Connection conn = util.DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GetCategorySuccessRate()}")) {
            
            ResultSet rs = cstmt.executeQuery();
            
            System.out.println("\n--- KATEGORİ BAZLI BAŞARI RAPORU ---");
            System.out.println("Kategori | Toplam Şikayet | Çözülen | Başarı %");
            System.out.println("-----------------------------------------------");
            
            while (rs.next()) {
                String kategori = rs.getString("Kategori");
                int toplam = rs.getInt("Toplam_Sikayet");
                int cozulen = rs.getInt("Cozulen_Sikayet");
                double basari = rs.getDouble("Basari_Yuzdesi");
                
                System.out.printf("%-15s | %-15d | %-7d | %.2f%%\n", 
                    kategori, toplam, cozulen, basari);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }
    
    /**
     * Belirli bir kategorideki bekleyen şikayetleri getirir (Stored Procedure kullanarak)
     * GetPendingIssuesByCategory() stored procedure'ını kullanır
     * 
     * @param categoryId Kategori ID'si
     * @return Bekleyen şikayetler listesi
     * @author Elif
     */
    public List<Issue> getPendingIssuesByCategory(int categoryId) {
        List<Issue> issues = new ArrayList<>();
        try (Connection conn = util.DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GetPendingIssuesByCategory(?)}")) {
            
            cstmt.setInt(1, categoryId);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                Issue issue = new Issue();
                issue.setIssueId(rs.getInt("issue_id"));
                issue.setTitle(rs.getString("title"));
                issue.setDescription(rs.getString("description"));
                issue.setStatus(rs.getString("status"));
                issues.add(issue);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return issues;
    }
    
    /**
     * Kullanıcının duruma göre şikayetlerini getirir (Stored Procedure kullanarak)
     * GetUserIssuesByStatus() stored procedure'ını kullanır
     * 
     * @param userId Kullanıcı ID'si
     * @param status Şikayet durumu (Yeni, İnceleniyor, Çözüldü)
     * @return Kullanıcının şikayetleri listesi
     * @author Elif
     */
    public List<Issue> getUserIssuesByStatus(int userId, String status) {
        List<Issue> issues = new ArrayList<>();
        try (Connection conn = util.DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GetUserIssuesByStatus(?, ?)}")) {
            
            cstmt.setInt(1, userId);
            cstmt.setString(2, status);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                Issue issue = new Issue();
                issue.setIssueId(rs.getInt("issue_id"));
                issue.setTitle(rs.getString("title"));
                issue.setDescription(rs.getString("description"));
                issue.setStatus(rs.getString("status"));
                issues.add(issue);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return issues;
    }
    
    /**
     * Son 30 günün günlük istatistiklerini gösterir (Stored Procedure kullanarak)
     * GetMonthlyStats() stored procedure'ını kullanır
     * 
     * @author Elif
     */
    public void printMonthlyStats() {
        try (Connection conn = util.DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GetMonthlyStats()}")) {
            
            ResultSet rs = cstmt.executeQuery();
            
            System.out.println("\n--- SON 30 GÜNÜN İSTATİSTİKLERİ ---");
            System.out.println("Tarih | Günlük Şikayet | Çözülen");
            System.out.println("-----------------------------------");
            
            while (rs.next()) {
                String tarih = rs.getString("tarih");
                int gunluk = rs.getInt("gunluk_sikayet_sayisi");
                int cozulen = rs.getInt("cozulen_sayisi");
                
                System.out.printf("%s | %-15d | %d\n", tarih, gunluk, cozulen);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }
}