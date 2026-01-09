-- Veritabanındaki tekrar eden örnek verileri temizleme scripti
-- Bu script, aynı örneklerden oluşan tekrarları temizler

USE akilli_sehir_db;

-- Önce mevcut durumu kontrol et
SELECT 'Mevcut şikayet sayısı:' as Info, COUNT(*) as Count FROM Issues;
SELECT 'Mevcut başvuru sayısı:' as Info, COUNT(*) as Count FROM Applications;
SELECT 'Mevcut proje sayısı:' as Info, COUNT(*) as Count FROM Projects;

-- 1. Tekrar eden şikayetleri temizle (aynı title, description ve user_id'ye sahip olanlar)
-- Her benzersiz kombinasyondan sadece en yenisini tut (en yüksek issue_id), diğerlerini sil
DELETE i1 FROM Issues i1
INNER JOIN Issues i2 
WHERE i1.issue_id < i2.issue_id 
AND i1.title = i2.title 
AND (i1.description = i2.description OR (i1.description IS NULL AND i2.description IS NULL))
AND i1.user_id = i2.user_id;

-- 2. Aynı başlığa sahip çok fazla şikayet varsa (örn: "Park Temizliği" 10+ kez)
-- Her benzersiz başlık için maksimum 3 şikayet tut (en yeni olanlar)
-- Önce hangi başlıkların çok tekrar ettiğini bul
CREATE TEMPORARY TABLE IF NOT EXISTS temp_duplicate_titles AS
SELECT title, user_id, COUNT(*) as cnt
FROM Issues
GROUP BY title, user_id
HAVING COUNT(*) > 3;

-- Çok tekrar eden başlıklar için, her birinden sadece en yeni 3'ünü tut
DELETE i1 FROM Issues i1
INNER JOIN temp_duplicate_titles t ON i1.title = t.title AND i1.user_id = t.user_id
WHERE i1.issue_id NOT IN (
    SELECT issue_id FROM (
        SELECT issue_id 
        FROM Issues i2
        WHERE i2.title = i1.title AND i2.user_id = i1.user_id
        ORDER BY created_at DESC, issue_id DESC
        LIMIT 3
    ) as temp
);

DROP TEMPORARY TABLE IF EXISTS temp_duplicate_titles;

-- 3. Tekrar eden başvuruları temizle (aynı project_id, user_id ve notes'a sahip olanlar)
DELETE a1 FROM Applications a1
INNER JOIN Applications a2 
WHERE a1.application_id < a2.application_id 
AND a1.project_id = a2.project_id 
AND a1.user_id = a2.user_id 
AND (a1.notes = a2.notes OR (a1.notes IS NULL AND a2.notes IS NULL));

-- 4. Tüm projeleri sıfırla (temizle)
-- Önce projelere ait başvuruları sil
DELETE FROM Applications;

-- Sonra tüm projeleri sil
DELETE FROM Projects;

-- AUTO_INCREMENT'i sıfırla (yeni projeler 1'den başlasın)
ALTER TABLE Projects AUTO_INCREMENT = 1;
ALTER TABLE Applications AUTO_INCREMENT = 1;

-- Temizlik sonrası durumu kontrol et
SELECT 'Temizlik sonrası şikayet sayısı:' as Info, COUNT(*) as Count FROM Issues;
SELECT 'Temizlik sonrası başvuru sayısı:' as Info, COUNT(*) as Count FROM Applications;
SELECT 'Temizlik sonrası proje sayısı:' as Info, COUNT(*) as Count FROM Projects;

-- Kalan şikayetleri göster (başlık ve sayıları)
SELECT title, COUNT(*) as count, GROUP_CONCAT(DISTINCT user_id) as user_ids
FROM Issues
GROUP BY title
ORDER BY count DESC
LIMIT 20;

-- Sonuçları göster
SELECT 'Temizlik işlemi tamamlandı!' as Message;

