package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Veritabanı Kurulum Yardımcı Sınıfı
 * 
 * Bu sınıf, veritabanını otomatik olarak oluşturur ve SQL dosyalarını çalıştırır.
 * SQL dosyaları: schema.sql, seed_data.sql, triggers.sql, procedures.sql
 * 
 * @author Esma, Elif 
 */
public class DatabaseSetup {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "akilli_sehir_db";
    private static final String USER = "root";
    private static final String SQL_DIR = "sql";
    
    // Şifreyi DBConnection'dan al (tek yerden yönetim için)
    private static String getPassword() {
        // DBConnection.java'daki şifreyi buraya yazın (DBConnection ile aynı olmalı)
        return "@Lifesk26"; // MySQL şifreniz
    }
    
    /**
     * Veritabanını oluşturur ve SQL dosyalarını çalıştırır
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
            
            // SQL dosyalarını sırayla çalıştır
            System.out.println("📋 SQL dosyaları çalıştırılıyor...\n");
            
            // 1. Schema (Tablolar)
            System.out.println("📄 schema.sql çalıştırılıyor...");
            if (executeSqlFile(stmt, SQL_DIR + "/schema.sql")) {
                System.out.println("✅ Tablolar oluşturuldu!\n");
            } else {
                System.out.println("❌ Schema dosyası çalıştırılamadı!\n");
            }
            
            // 2. Seed Data (Örnek veriler)
            System.out.println("📄 seed_data.sql çalıştırılıyor...");
            if (executeSqlFile(stmt, SQL_DIR + "/seed_data.sql")) {
                System.out.println("✅ Örnek veriler eklendi!\n");
            } else {
                System.out.println("⚠️ Seed data dosyası çalıştırılamadı (veriler zaten mevcut olabilir)\n");
            }
            
            // 3. Triggers
            System.out.println("📄 triggers.sql çalıştırılıyor...");
            if (executeSqlFile(stmt, SQL_DIR + "/triggers.sql")) {
                System.out.println("✅ Trigger'lar oluşturuldu!\n");
            } else {
                System.out.println("⚠️ Trigger dosyası çalıştırılamadı\n");
            }
            
            // 4. Procedures
            System.out.println("📄 procedures.sql çalıştırılıyor...");
            if (executeSqlFile(stmt, SQL_DIR + "/procedures.sql")) {
                System.out.println("✅ Stored procedure'lar oluşturuldu!\n");
            } else {
                System.out.println("⚠️ Procedure dosyası çalıştırılamadı\n");
            }
            
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
     * SQL dosyasını okur ve çalıştırır
     * DELIMITER komutlarını ve çok satırlı SQL komutlarını destekler
     * 
     * @param stmt Statement nesnesi
     * @param filePath SQL dosyasının yolu
     * @return Başarılıysa true
     */
    private static boolean executeSqlFile(Statement stmt, String filePath) {
        try {
            // Dosya yolunu oluştur (proje kök dizininden)
            File sqlFile = new File(filePath);
            
            // Eğer dosya bulunamazsa, SmartCityPlatform klasörü altında ara
            if (!sqlFile.exists()) {
                sqlFile = new File("SmartCityPlatform/" + filePath);
            }
            
            if (!sqlFile.exists()) {
                System.out.println("⚠️ Dosya bulunamadı: " + filePath);
                return false;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
            StringBuilder sql = new StringBuilder();
            String line;
            String delimiter = ";";
            boolean inDelimiterBlock = false;
            String currentDelimiter = ";";
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Boş satırları ve yorumları atla
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                // DELIMITER komutunu işle
                if (line.toUpperCase().startsWith("DELIMITER")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length > 1) {
                        currentDelimiter = parts[1];
                        if (!currentDelimiter.equals(";")) {
                            inDelimiterBlock = true;
                        } else {
                            inDelimiterBlock = false;
                        }
                    }
                    continue;
                }
                
                // USE komutunu ayrı çalıştır (zaten USE yapıldığı için atlayabiliriz)
                if (line.toUpperCase().startsWith("USE ")) {
                    continue;
                }
                
                sql.append(line).append(" ");
                
                // Delimiter'a göre komutun bitip bitmediğini kontrol et
                if (line.endsWith(currentDelimiter)) {
                    String sqlCommand = sql.toString().trim();
                    // Delimiter'ı kaldır
                    if (sqlCommand.endsWith(currentDelimiter)) {
                        sqlCommand = sqlCommand.substring(0, sqlCommand.length() - currentDelimiter.length()).trim();
                    }
                    
                    if (!sqlCommand.isEmpty()) {
                        try {
                            // Çok satırlı komutlar için execute kullan
                            stmt.execute(sqlCommand);
                        } catch (Exception e) {
                            // Bazı hatalar normal olabilir (örn: zaten var olan trigger/procedure)
                            // Sadece kritik hataları göster
                            if (!e.getMessage().contains("already exists") && 
                                !e.getMessage().contains("Duplicate")) {
                                System.out.println("⚠️ SQL komutu hatası: " + e.getMessage());
                            }
                        }
                    }
                    sql.setLength(0); // StringBuilder'ı temizle
                }
            }
            
            reader.close();
            return true;
            
        } catch (Exception e) {
            System.out.println("❌ SQL dosyası okuma hatası (" + filePath + "): " + e.getMessage());
            return false;
        }
    }
}

