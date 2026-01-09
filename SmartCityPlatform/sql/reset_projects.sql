-- Projeleri sıfırlama scripti
-- Bu script, tüm projeleri ve başvuruları temizler

USE akilli_sehir_db;

-- Önce mevcut durumu kontrol et
SELECT 'Mevcut proje sayısı:' as Info, COUNT(*) as Count FROM Projects;
SELECT 'Mevcut başvuru sayısı:' as Info, COUNT(*) as Count FROM Applications;

-- Projelere ait başvuruları sil
DELETE FROM Applications;

-- Tüm projeleri sil
DELETE FROM Projects;

-- AUTO_INCREMENT'i sıfırla (yeni projeler 1'den başlasın)
ALTER TABLE Projects AUTO_INCREMENT = 1;
ALTER TABLE Applications AUTO_INCREMENT = 1;

-- Temizlik sonrası durumu kontrol et
SELECT 'Temizlik sonrası proje sayısı:' as Info, COUNT(*) as Count FROM Projects;
SELECT 'Temizlik sonrası başvuru sayısı:' as Info, COUNT(*) as Count FROM Applications;

-- Sonuçları göster
SELECT 'Projeler sıfırlandı!' as Message;

