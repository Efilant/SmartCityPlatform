package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.User;
import util.DBConnection;

public class UserDAO {
    
    public void saveUser(User user) {
        String query = "INSERT INTO Users (username, password_hash, role, full_name) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getFullName());
            
            pstmt.executeUpdate();
            System.out.println("Kullanıcı başarıyla kaydedildi! 🎉");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public User login(String username, String password) {
        String query = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setFullName(rs.getString("full_name"));
                return user; // Kullanıcı bulundu
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Kullanıcı bulunamadı veya şifre yanlış
    }
    public boolean registerUser(String username, String password, String fullName) {
    // Yeni kayıt olan herkes varsayılan olarak 'CITIZEN' olur
    String query = "INSERT INTO Users (username, password_hash, role, full_name) VALUES (?, ?, 'CITIZEN', ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setString(1, username);
        pstmt.setString(2, password); // Gerçek projede şifreleme (Encryption) önerilir 
        pstmt.setString(3, fullName);

        int result = pstmt.executeUpdate();
        return result > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    public User findById(int id) {
    String query = "SELECT * FROM Users WHERE user_id = ?";
    try (Connection conn = util.DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setUsername(rs.getString("username"));
            user.setRole(rs.getString("role"));
            return user;
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return null;
}
}