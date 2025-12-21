package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;

/**
 * Veritabanı Kurulum Yardımcı Sınıfı
 * 
 * Bu sınıf, veritabanını otomatik olarak oluşturur ve tabloları kurar.
 * 
 * @author Esma
 */
public class DatabaseSetup {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "akilli_sehir_db";
    private static final String USER = "javauser";
    // Şifreyi DBConnection'dan al (tek yerden yönetim için)
    private static String getPassword() {
        // DBConnection.java'daki şifreyi buraya yazın (DBConnection ile aynı olmalı)
        return "java123"; // MySQL şifreniz
    }
    
    /**
     * Veritabanını oluşturur ve tabloları kurar
     * @return Başarılıysa true
     */
    public static boolean setupDatabase() {
        try {
            // MySQL sürücüsünü yükle
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Önce veritabanı olmadan bağlan (veritabanını oluşturmak için)
            Connection conn = DriverManager.getConnection(DB_URL, USER, getPassword());
            Statement stmt = conn.createStatement();
            
            // Veritabanının var olup olmadığını kontrol et
            ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE '" + DB_NAME + "'");
            
            if (!rs.next()) {
                // Veritabanı yoksa oluştur
                System.out.println("📦 Veritabanı oluşturuluyor...");
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                System.out.println("✅ Veritabanı oluşturuldu: " + DB_NAME);
            } else {
                System.out.println("✅ Veritabanı zaten mevcut: " + DB_NAME);
            }
            
            // Veritabanını seç
            stmt.executeUpdate("USE " + DB_NAME);
            
            // Tabloları oluştur
            System.out.println("📋 Tablolar oluşturuluyor...");
            
            // Users tablosu
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "role ENUM('CITIZEN', 'ADMIN') NOT NULL, " +
                "full_name VARCHAR(100)" +
                ")"
            );
            
            // Categories tablosu
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Categories (" +
                "category_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "responsible_unit VARCHAR(100)" +
                ")"
            );
            
            // Issues tablosu
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Issues (" +
                "issue_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT, " +
                "category_id INT, " +
                "title VARCHAR(100) NOT NULL, " +
                "description TEXT, " +
                "status ENUM('Yeni', 'İnceleniyor', 'Çözüldü') DEFAULT 'Yeni', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id), " +
                "FOREIGN KEY (category_id) REFERENCES Categories(category_id)" +
                ")"
            );
            
            // Projects tablosu
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Projects (" +
                "project_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(150) NOT NULL, " +
                "description TEXT, " +
                "start_date DATE, " +
                "end_date DATE, " +
                "status ENUM('Açık', 'Kapalı', 'Tamamlandı') DEFAULT 'Açık', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Applications tablosu
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Applications (" +
                "application_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "project_id INT, " +
                "user_id INT, " +
                "application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "status ENUM('Beklemede', 'Onaylandı', 'Reddedildi') DEFAULT 'Beklemede', " +
                "notes TEXT, " +
                "FOREIGN KEY (project_id) REFERENCES Projects(project_id), " +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id)" +
                ")"
            );
            
            System.out.println("✅ Tüm tablolar oluşturuldu!");
            
            // Örnek verileri ekle (eğer yoksa)
            insertSampleData(stmt);
            
            stmt.close();
            conn.close();
            
            System.out.println("🎉 Veritabanı kurulumu tamamlandı!\n");
            return true;
            
        } catch (Exception e) {
            System.out.println("❌ Veritabanı kurulum hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Örnek verileri ekler
     */
    private static void insertSampleData(Statement stmt) throws Exception {
        System.out.println("📝 Örnek veriler ekleniyor...");
        
        // Kullanıcılar (eğer yoksa)
        try {
            stmt.executeUpdate(
                "INSERT IGNORE INTO Users (username, password_hash, role, full_name) VALUES " +
                "('admin_elif', '123456', 'ADMIN', 'Elif Admin'), " +
                "('vatandas_ali', '654321', 'CITIZEN', 'Ali Vatandas')"
            );
        } catch (Exception e) {
            // Zaten varsa hata verme
        }
        
        // Kategoriler
        try {
            stmt.executeUpdate(
                "INSERT IGNORE INTO Categories (name, responsible_unit) VALUES " +
                "('Ulaşım', 'Ulaşım Daire Başkanlığı'), " +
                "('Çevre ve Temizlik', 'Atık Yönetimi Birimi'), " +
                "('Altyapı', 'Fen İşleri Müdürlüğü'), " +
                "('Sosyal Hizmetler', 'Sosyal İşler Daire Başkanlığı')"
            );
        } catch (Exception e) {}
        
        // Projeler
        try {
            stmt.executeUpdate(
                "INSERT IGNORE INTO Projects (title, description, start_date, end_date, status) VALUES " +
                "('Akıllı Bisiklet Yolu', 'Şehir merkezine 10km kesintisiz bisiklet yolu yapımı.', '2024-01-01', '2024-06-01', 'Açık'), " +
                "('Sıfır Atık Kampanyası', 'Mahalle bazlı geri dönüşüm eğitimi ve kutu dağıtımı.', '2024-02-15', '2024-05-15', 'Açık')"
            );
        } catch (Exception e) {}
        
        System.out.println("✅ Örnek veriler eklendi!");
    }
}

