package dao;

import util.DBConnection;
import models.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    
    // Tüm kategorileri getirir (Şikayet oluşturma ekranında açılır menü için)
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM Categories";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("category_id"),
                    rs.getString("name"),
                    rs.getString("responsible_unit")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // ID'ye göre kategori bulma
    public Category findById(int id) {
        String query = "SELECT * FROM Categories WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("category_id"), rs.getString("name"), rs.getString("responsible_unit"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}