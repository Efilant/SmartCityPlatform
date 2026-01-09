-- Tüm Duplicate'leri Temizleme Scripti
-- Bu script tüm duplicate kayıtları siler

USE akilli_sehir_db;

-- Foreign key constraint'leri devre dışı bırak (daha hızlı silme için)
SET FOREIGN_KEY_CHECKS = 0;

-- 1. TÜM VERİLERİ SİL (Duplicate kontrolü yapmadan)
DELETE FROM Applications;
DELETE FROM Issues;
DELETE FROM Projects;
DELETE FROM Categories;
DELETE FROM Users;

-- 2. AUTO_INCREMENT'leri sıfırla
ALTER TABLE Applications AUTO_INCREMENT = 1;
ALTER TABLE Issues AUTO_INCREMENT = 1;
ALTER TABLE Projects AUTO_INCREMENT = 1;
ALTER TABLE Categories AUTO_INCREMENT = 1;
ALTER TABLE Users AUTO_INCREMENT = 1;

-- Foreign key constraint'leri tekrar aktif et
SET FOREIGN_KEY_CHECKS = 1;

-- Kontrol: Tüm tablolar boş olmalı
SELECT 
    'Users' AS TableName, COUNT(*) AS Count FROM Users
UNION ALL
SELECT 'Categories', COUNT(*) FROM Categories
UNION ALL
SELECT 'Issues', COUNT(*) FROM Issues
UNION ALL
SELECT 'Projects', COUNT(*) FROM Projects
UNION ALL
SELECT 'Applications', COUNT(*) FROM Applications;

SELECT 'Tüm veriler silindi! Şimdi seed_data.sql dosyasını çalıştırın.' AS Status;

