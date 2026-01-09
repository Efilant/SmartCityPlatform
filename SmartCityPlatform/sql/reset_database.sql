-- Veritabanını Sıfırdan Kurma Scripti
-- Bu script tüm tabloları siler ve yeniden oluşturur

USE akilli_sehir_db;

-- Foreign key constraint'leri devre dışı bırak
SET FOREIGN_KEY_CHECKS = 0;

-- Tüm tabloları sil (ters sırada - foreign key'ler için)
DROP TABLE IF EXISTS Applications;
DROP TABLE IF EXISTS Issues;
DROP TABLE IF EXISTS Projects;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS Users;

-- Stored procedure'ları sil
DROP PROCEDURE IF EXISTS GetSystemStats;
DROP PROCEDURE IF EXISTS GetCategorySuccessRate;
DROP PROCEDURE IF EXISTS GetUserActivitySummary;
DROP PROCEDURE IF EXISTS GetPendingIssuesByCategory;
DROP PROCEDURE IF EXISTS GetUserIssuesByStatus;
DROP PROCEDURE IF EXISTS GetMonthlyStats;

-- Trigger'ları sil
DROP TRIGGER IF EXISTS update_apps_on_project_close;
DROP TRIGGER IF EXISTS check_issue_status_flow;

-- Foreign key constraint'leri tekrar aktif et
SET FOREIGN_KEY_CHECKS = 1;

-- AUTO_INCREMENT'leri sıfırla (gerekirse)
-- ALTER TABLE Users AUTO_INCREMENT = 1;
-- ALTER TABLE Categories AUTO_INCREMENT = 1;
-- ALTER TABLE Issues AUTO_INCREMENT = 1;
-- ALTER TABLE Projects AUTO_INCREMENT = 1;
-- ALTER TABLE Applications AUTO_INCREMENT = 1;

SELECT 'Veritabanı temizlendi. Şimdi schema.sql, procedures.sql, triggers.sql ve seed_data.sql dosyalarını çalıştırın.' AS Status;

-- Not: add_priority_column.sql artık gerekli değil çünkü priority kolonu schema.sql'e eklendi

