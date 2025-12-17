package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Issue;
import util.DBConnection;

public class IssueDAO {
    
        
    public void createIssue(Issue issue) {
    String query = "INSERT INTO Issues (user_id, title, description, status) VALUES (?, ?, ?, 'Yeni')";

    try (Connection conn = util.DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setInt(1, issue.getUserId());
        pstmt.setString(2, issue.getTitle());
        pstmt.setString(3, issue.getDescription());

        pstmt.executeUpdate();
        System.out.println("Şikayetiniz başarıyla sisteme iletildi! ✅");

    } catch (SQLException e) {
        System.out.println("Şikayet oluşturulurken bir hata oluştu: " + e.getMessage());
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
    public void printCategoryReport() {
    String query = "SELECT c.name, COUNT(i.issue_id) as total " +
                   "FROM Categories c " +
                   "LEFT JOIN Issues i ON c.category_id = i.category_id " +
                   "GROUP BY c.name";

    try (Connection conn = util.DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        
        System.out.println("\n--- KATEGORİ BAZLI ŞİKAYET RAPORU ---");
        while (rs.next()) {
            System.out.println(rs.getString("name") + ": " + rs.getInt("total") + " şikayet");
        }
    } catch (SQLException e) { e.printStackTrace(); }
}
}