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
    /**
     * Tüm şikayetleri getirir (filtreleme olmadan)
     */
    public List<Issue> getAllIssues() {
        return getAllIssues(null, null);
    }
    
    /**
     * Şikayetleri kategori ve durum filtresine göre getirir
     * @param categoryId Kategori ID'si (null ise tüm kategoriler)
     * @param status Durum (null ise tüm durumlar)
     * @return Filtrelenmiş şikayetler listesi
     */
    public List<Issue> getAllIssues(Integer categoryId, String status) {
        List<Issue> issues = new ArrayList<>();
        
        // PreparedStatement ile güvenli sorgu - NULL parametreleri doğru handle et
        // Kullanıcının önerdiği yaklaşım: (? IS NULL OR column = ?)
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Issues WHERE 1=1");
        
        // Kategori filtresi: NULL ise tüm kategoriler, değilse sadece o kategori
        if (categoryId != null && categoryId > 0) {
            queryBuilder.append(" AND category_id = ?");
        } else {
            // categoryId null veya 0 ise, hem NULL hem de tüm kategorileri getir
            // Bu durumda WHERE şartı eklenmez (zaten WHERE 1=1 var)
        }
        
        // Durum filtresi: NULL veya "Tümü" ise tüm durumlar
        if (status != null && !status.trim().isEmpty() && !status.equals("Tümü")) {
            queryBuilder.append(" AND status = ?");
        }
        
        queryBuilder.append(" ORDER BY issue_id DESC");
        
        String query = queryBuilder.toString();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if (conn == null) {
                return issues;
            }
            
            pstmt = conn.prepareStatement(query);
            
            int paramIndex = 1;
            if (categoryId != null && categoryId > 0) {
                pstmt.setInt(paramIndex++, categoryId);
            }
            
            if (status != null && !status.trim().isEmpty() && !status.equals("Tümü")) {
                pstmt.setString(paramIndex++, status);
            }
            
            rs = pstmt.executeQuery();
            
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Connection'ları düzgün kapat
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Connection kapatılırken hata: " + e.getMessage());
            }
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
            throw new RuntimeException("Şikayet durumu güncellenemedi: " + e.getMessage(), e);
        }
    }
    
    public void updateIssuePriority(int issueId, String priority) {
        String query = "UPDATE Issues SET priority = ? WHERE issue_id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, priority);
            pstmt.setInt(2, issueId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Şikayet önceliği başarıyla güncellendi! ✅");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Şikayet önceliği güncellenemedi: " + e.getMessage(), e);
        }
    }
    public List<Issue> findAllByUserId(int userId) {
        List<Issue> issues = new ArrayList<>();
        // Tüm durumlar için şikayetleri getir - stored procedure'ları kullanarak
        // Her durum için ayrı ayrı çağırıp birleştir
        String[] statuses = {"Yeni", "İnceleniyor", "Çözüldü"};
        
        try (Connection conn = util.DBConnection.getConnection()) {
            for (String status : statuses) {
                try (CallableStatement cstmt = conn.prepareCall("{CALL GetUserIssuesByStatus(?, ?)}")) {
                    cstmt.setInt(1, userId);
                    cstmt.setString(2, status);
                    ResultSet rs = cstmt.executeQuery();
                    
                    while (rs.next()) {
                        Issue issue = new Issue();
                        issue.setIssueId(rs.getInt("issue_id"));
                        issue.setUserId(userId); // userId'yi set et
                        issue.setTitle(rs.getString("title"));
                        issue.setDescription(rs.getString("description"));
                        issue.setStatus(rs.getString("status"));
                        if (rs.getTimestamp("created_at") != null) {
                            issue.setCreatedAt(rs.getTimestamp("created_at").toString());
                        }
                        // category_id stored procedure'da yok ama ekleyebiliriz
                        issues.add(issue);
                    }
                }
            }
            System.out.println("✅ findAllByUserId: " + issues.size() + " şikayet bulundu (userId: " + userId + ")");
        } catch (SQLException e) { 
            System.err.println("❌ findAllByUserId hatası (userId: " + userId + "): " + e.getMessage());
            e.printStackTrace(); 
        }
        return issues;
    }
    /**
     * Kategori başarı oranlarını getirir (Stored Procedure kullanarak)
     * GetCategorySuccessRate() stored procedure'ını kullanır
     * 
     * @return List of category report maps
     * @author Elif
     */
    public List<java.util.Map<String, Object>> getCategoryReport() {
        List<java.util.Map<String, Object>> report = new ArrayList<>();
        try (Connection conn = util.DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GetCategorySuccessRate()}")) {
            
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                java.util.Map<String, Object> categoryData = new java.util.HashMap<>();
                categoryData.put("category", rs.getString("Kategori"));
                categoryData.put("totalIssues", rs.getInt("Toplam_Sikayet"));
                categoryData.put("resolvedIssues", rs.getInt("Cozulen_Sikayet"));
                categoryData.put("successRate", rs.getDouble("Basari_Yuzdesi"));
                report.add(categoryData);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return report;
    }
    
    /**
     * Eski metod uyumluluk için (deprecated - console'a yazdırır)
     * @deprecated Use getCategoryReport() instead
     */
    @Deprecated
    public void printCategoryReport() {
        List<java.util.Map<String, Object>> report = getCategoryReport();
        System.out.println("\n--- KATEGORİ BAZLI BAŞARI RAPORU ---");
        System.out.println("Kategori | Toplam Şikayet | Çözülen | Başarı %");
        System.out.println("-----------------------------------------------");
        
        for (java.util.Map<String, Object> data : report) {
            System.out.printf("%-15s | %-15d | %-7d | %.2f%%\n", 
                data.get("category"), 
                data.get("totalIssues"), 
                data.get("resolvedIssues"), 
                data.get("successRate"));
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
                issue.setUserId(userId); // userId'yi set et
                issue.setTitle(rs.getString("title"));
                issue.setDescription(rs.getString("description"));
                issue.setStatus(rs.getString("status"));
                if (rs.getTimestamp("created_at") != null) {
                    issue.setCreatedAt(rs.getTimestamp("created_at").toString());
                }
                // category_id stored procedure'da yok ama ekleyebiliriz
                issues.add(issue);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return issues;
    }
    
    /**
     * Son 30 günün günlük istatistiklerini getirir (Stored Procedure kullanarak)
     * GetMonthlyStats() stored procedure'ını kullanır
     * 
     * @return List of daily statistics maps
     * @author Elif
     */
    public List<java.util.Map<String, Object>> getMonthlyStats() {
        List<java.util.Map<String, Object>> stats = new ArrayList<>();
        try (Connection conn = util.DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GetMonthlyStats()}")) {
            
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                java.util.Map<String, Object> dayStats = new java.util.HashMap<>();
                dayStats.put("date", rs.getString("tarih"));
                dayStats.put("dailyIssues", rs.getInt("gunluk_sikayet_sayisi"));
                dayStats.put("resolvedCount", rs.getInt("cozulen_sayisi"));
                stats.add(dayStats);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return stats;
    }
    
    /**
     * Eski metod uyumluluk için (deprecated - console'a yazdırır)
     * @deprecated Use getMonthlyStats() instead
     */
    @Deprecated
    public void printMonthlyStats() {
        List<java.util.Map<String, Object>> stats = getMonthlyStats();
        System.out.println("\n--- SON 30 GÜNÜN İSTATİSTİKLERİ ---");
        System.out.println("Tarih | Günlük Şikayet | Çözülen");
        System.out.println("-----------------------------------");
        
        for (java.util.Map<String, Object> dayStats : stats) {
            System.out.printf("%s | %-15d | %d\n", 
                dayStats.get("date"), 
                dayStats.get("dailyIssues"), 
                dayStats.get("resolvedCount"));
        }
    }
}