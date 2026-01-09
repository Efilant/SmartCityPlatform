-- Tüm Verileri Zorla Temizleme Scripti
-- Bu script tüm verileri siler ve AUTO_INCREMENT'leri sıfırlar

USE akilli_sehir_db;

-- Foreign key constraint'leri devre dışı bırak
SET FOREIGN_KEY_CHECKS = 0;

-- TÜM VERİLERİ SİL (Foreign key'ler devre dışı olduğu için sıra önemli değil)
DELETE FROM Applications;
DELETE FROM Issues;
DELETE FROM Projects;
DELETE FROM Categories;
DELETE FROM Users;

-- AUTO_INCREMENT'leri sıfırla
ALTER TABLE Applications AUTO_INCREMENT = 1;
ALTER TABLE Issues AUTO_INCREMENT = 1;
ALTER TABLE Projects AUTO_INCREMENT = 1;
ALTER TABLE Categories AUTO_INCREMENT = 1;
ALTER TABLE Users AUTO_INCREMENT = 1;

-- Foreign key constraint'leri tekrar aktif et
SET FOREIGN_KEY_CHECKS = 1;

-- Kontrol: Kaç kayıt kaldı?
SELECT 
    (SELECT COUNT(*) FROM Users) AS users_count,
    (SELECT COUNT(*) FROM Categories) AS categories_count,
    (SELECT COUNT(*) FROM Issues) AS issues_count,
    (SELECT COUNT(*) FROM Projects) AS projects_count,
    (SELECT COUNT(*) FROM Applications) AS applications_count;

SELECT 'Tüm veriler temizlendi ve AUTO_INCREMENT değerleri sıfırlandı!' AS Status;

