package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Category;
import util.DBConnection;

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
    
    /**
     * En çok şikayet alan kategorileri getirir (Stored Procedure kullanarak)
     * GetTopCategories() stored procedure'ını kullanır
     * 
     * @param limit Kaç kategori gösterilecek (örn: 5)
     * @author Elif
     */
    public void printTopCategories(int limit) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GetTopCategories(?)}")) {
            
            cstmt.setInt(1, limit);
            ResultSet rs = cstmt.executeQuery();
            
            System.out.println("\n--- EN ÇOK ŞİKAYET ALAN KATEGORİLER (Top " + limit + ") ---");
            System.out.println("Kategori | Toplam Şikayet | Çözülen | Sorumlu Birim");
            System.out.println("--------------------------------------------------------");
            
            while (rs.next()) {
                String kategori = rs.getString("kategori_adi");
                int toplam = rs.getInt("toplam_sikayet");
                int cozulen = rs.getInt("cozulen_sikayet");
                String sorumlu = rs.getString("sorumlu_birim");
                
                System.out.printf("%-15s | %-15d | %-7d | %s\n", 
                    kategori, toplam, cozulen, sorumlu != null ? sorumlu : "Belirtilmemiş");
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }
}