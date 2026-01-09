package util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * DuplicateCleaner - Veritabanındaki duplicate kayıtları temizler
 */
public class DuplicateCleaner {
    
    public static boolean cleanDuplicates() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("\n========================================");
            System.out.println("  DUPLICATE TEMİZLEME İŞLEMİ BAŞLATILIYOR");
            System.out.println("========================================\n");
            
            // Mevcut durumu kontrol et
            System.out.println("Mevcut durum kontrol ediliyor...");
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Issues");
            if (rs.next()) {
                System.out.println("Mevcut şikayet sayısı: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Applications");
            if (rs.next()) {
                System.out.println("Mevcut başvuru sayısı: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Categories");
            if (rs.next()) {
                System.out.println("Mevcut kategori sayısı: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Projects");
            if (rs.next()) {
                System.out.println("Mevcut proje sayısı: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Users");
            if (rs.next()) {
                System.out.println("Mevcut kullanıcı sayısı: " + rs.getInt("count"));
            }
            
            // 1. Tekrar eden şikayetleri temizle
            System.out.println("\n1. Tekrar eden şikayetler temizleniyor...");
            int deletedIssues = stmt.executeUpdate(
                "DELETE i1 FROM Issues i1 " +
                "INNER JOIN Issues i2 " +
                "WHERE i1.issue_id < i2.issue_id " +
                "AND i1.title = i2.title " +
                "AND (i1.description = i2.description OR (i1.description IS NULL AND i2.description IS NULL)) " +
                "AND i1.user_id = i2.user_id"
            );
            System.out.println("   " + deletedIssues + " duplicate şikayet silindi.");
            
            // 2. Aynı başlığa sahip çok fazla şikayet varsa, her birinden sadece en yeni 3'ünü tut
            System.out.println("\n2. Aynı başlığa sahip fazla şikayetler temizleniyor...");
            
            // Geçici tablo oluştur
            stmt.executeUpdate(
                "CREATE TEMPORARY TABLE IF NOT EXISTS temp_duplicate_titles AS " +
                "SELECT title, user_id, COUNT(*) as cnt " +
                "FROM Issues " +
                "GROUP BY title, user_id " +
                "HAVING COUNT(*) > 3"
            );
            
            // Çok tekrar eden başlıklar için, her birinden sadece en yeni 3'ünü tut
            int deletedExtraIssues = stmt.executeUpdate(
                "DELETE i1 FROM Issues i1 " +
                "INNER JOIN temp_duplicate_titles t ON i1.title = t.title AND i1.user_id = t.user_id " +
                "WHERE i1.issue_id NOT IN (" +
                "    SELECT issue_id FROM (" +
                "        SELECT issue_id " +
                "        FROM Issues i2 " +
                "        WHERE i2.title = i1.title AND i2.user_id = i1.user_id " +
                "        ORDER BY created_at DESC, issue_id DESC " +
                "        LIMIT 3" +
                "    ) as temp" +
                ")"
            );
            System.out.println("   " + deletedExtraIssues + " fazla şikayet silindi.");
            
            // Geçici tabloyu sil
            stmt.executeUpdate("DROP TEMPORARY TABLE IF EXISTS temp_duplicate_titles");
            
            // 3. Tekrar eden başvuruları temizle
            System.out.println("\n3. Tekrar eden başvurular temizleniyor...");
            int deletedApplications = stmt.executeUpdate(
                "DELETE a1 FROM Applications a1 " +
                "INNER JOIN Applications a2 " +
                "WHERE a1.application_id < a2.application_id " +
                "AND a1.project_id = a2.project_id " +
                "AND a1.user_id = a2.user_id " +
                "AND (a1.notes = a2.notes OR (a1.notes IS NULL AND a2.notes IS NULL))"
            );
            System.out.println("   " + deletedApplications + " duplicate başvuru silindi.");
            
            // 4. Tekrar eden kategorileri temizle (aynı name'e sahip olanlar)
            System.out.println("\n4. Tekrar eden kategoriler temizleniyor...");
            
            // Geçici tablo: duplicate kategori isimlerini bul ve tutulacak ID'yi belirle
            stmt.executeUpdate(
                "CREATE TEMPORARY TABLE IF NOT EXISTS temp_duplicate_categories AS " +
                "SELECT name, MIN(category_id) as keep_id, COUNT(*) as cnt " +
                "FROM Categories " +
                "GROUP BY name " +
                "HAVING COUNT(*) > 1"
            );
            
            // Duplicate kategoriler için Issues referanslarını güncelle
            // Her duplicate kategori için, Issues'deki referansları en düşük ID'ye yönlendir
            try {
                // Önce tüm duplicate kategori ID'lerini ve keep_id'lerini al
                java.sql.PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE Issues SET category_id = ? WHERE category_id = ?"
                );
                
                ResultSet dupRs = stmt.executeQuery(
                    "SELECT c.category_id, t.keep_id " +
                    "FROM Categories c " +
                    "INNER JOIN temp_duplicate_categories t ON c.name = t.name " +
                    "WHERE c.category_id != t.keep_id"
                );
                
                int updatedRefs = 0;
                while (dupRs.next()) {
                    int oldId = dupRs.getInt("category_id");
                    int newId = dupRs.getInt("keep_id");
                    updateStmt.setInt(1, newId);
                    updateStmt.setInt(2, oldId);
                    updatedRefs += updateStmt.executeUpdate();
                }
                updateStmt.close();
                
                if (updatedRefs > 0) {
                    System.out.println("   " + updatedRefs + " şikayet referansı güncellendi.");
                }
            } catch (Exception e) {
                System.out.println("   ⚠️  Referans güncelleme sırasında hata: " + e.getMessage());
            }
            
            // Artık kullanılmayan duplicate kategorileri sil
            int deletedCategories = stmt.executeUpdate(
                "DELETE c FROM Categories c " +
                "INNER JOIN temp_duplicate_categories t ON c.name = t.name " +
                "WHERE c.category_id != t.keep_id"
            );
            System.out.println("   " + deletedCategories + " duplicate kategori silindi.");
            
            // Geçici tabloyu sil
            stmt.executeUpdate("DROP TEMPORARY TABLE IF EXISTS temp_duplicate_categories");
            
            // 5. Tekrar eden projeleri temizle (aynı title'a sahip olanlar)
            System.out.println("\n5. Tekrar eden projeler temizleniyor...");
            
            // Geçici tablo: duplicate proje başlıklarını bul
            stmt.executeUpdate(
                "CREATE TEMPORARY TABLE IF NOT EXISTS temp_duplicate_projects AS " +
                "SELECT title, MIN(project_id) as keep_id, COUNT(*) as cnt " +
                "FROM Projects " +
                "WHERE title IS NOT NULL AND title != '' " +
                "GROUP BY title " +
                "HAVING COUNT(*) > 1"
            );
            
            // Duplicate projeler için Applications referanslarını güncelle
            try {
                java.sql.PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE Applications SET project_id = ? WHERE project_id = ?"
                );
                
                ResultSet dupRs = stmt.executeQuery(
                    "SELECT p.project_id, t.keep_id " +
                    "FROM Projects p " +
                    "INNER JOIN temp_duplicate_projects t ON p.title = t.title " +
                    "WHERE p.project_id != t.keep_id"
                );
                
                int updatedRefs = 0;
                while (dupRs.next()) {
                    int oldId = dupRs.getInt("project_id");
                    int newId = dupRs.getInt("keep_id");
                    updateStmt.setInt(1, newId);
                    updateStmt.setInt(2, oldId);
                    updatedRefs += updateStmt.executeUpdate();
                }
                updateStmt.close();
                
                if (updatedRefs > 0) {
                    System.out.println("   " + updatedRefs + " başvuru referansı güncellendi.");
                }
            } catch (Exception e) {
                System.out.println("   ⚠️  Referans güncelleme sırasında hata: " + e.getMessage());
            }
            
            // Artık kullanılmayan duplicate projeleri sil
            int deletedProjects = stmt.executeUpdate(
                "DELETE p FROM Projects p " +
                "INNER JOIN temp_duplicate_projects t ON p.title = t.title " +
                "WHERE p.project_id != t.keep_id"
            );
            System.out.println("   " + deletedProjects + " duplicate proje silindi.");
            
            // Geçici tabloyu sil
            stmt.executeUpdate("DROP TEMPORARY TABLE IF EXISTS temp_duplicate_projects");
            
            // Temizlik sonrası durumu kontrol et
            System.out.println("\n========================================");
            System.out.println("  TEMİZLEME SONRASI DURUM");
            System.out.println("========================================\n");
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Issues");
            if (rs.next()) {
                System.out.println("Kalan şikayet sayısı: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Applications");
            if (rs.next()) {
                System.out.println("Kalan başvuru sayısı: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Categories");
            if (rs.next()) {
                System.out.println("Kalan kategori sayısı: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Projects");
            if (rs.next()) {
                System.out.println("Kalan proje sayısı: " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Users");
            if (rs.next()) {
                System.out.println("Kalan kullanıcı sayısı: " + rs.getInt("count"));
            }
            
            // Duplicate kontrolü - kalan duplicate'leri göster
            System.out.println("\n--- KALAN DUPLICATE KONTROLÜ ---");
            rs = stmt.executeQuery(
                "SELECT name, COUNT(*) as cnt FROM Categories GROUP BY name HAVING COUNT(*) > 1"
            );
            boolean hasCategoryDuplicates = false;
            while (rs.next()) {
                if (!hasCategoryDuplicates) {
                    System.out.println("⚠️  Hala duplicate kategoriler var:");
                    hasCategoryDuplicates = true;
                }
                System.out.println("   - " + rs.getString("name") + " (" + rs.getInt("cnt") + " adet)");
            }
            if (!hasCategoryDuplicates) {
                System.out.println("✅ Kategori duplicate'leri temizlendi.");
            }
            
            System.out.println("\n✅ Duplicate temizleme işlemi tamamlandı!\n");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Duplicate temizleme sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

