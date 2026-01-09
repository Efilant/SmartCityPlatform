-- TAM RESET: Tüm Tabloları Sil ve Yeniden Oluştur
-- Bu script veritabanını tamamen sıfırlar

USE akilli_sehir_db;

-- Foreign key constraint'leri devre dışı bırak
SET FOREIGN_KEY_CHECKS = 0;

-- TÜM TABLOLARI SİL (ters sırada - foreign key'ler için)
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

SELECT 'Tüm tablolar, procedure\'lar ve trigger\'lar silindi!' AS Status;

