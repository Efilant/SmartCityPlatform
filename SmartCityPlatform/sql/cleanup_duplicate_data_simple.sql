-- Veritabanındaki tekrar eden örnek verileri temizleme scripti (Basitleştirilmiş)
-- Bu script, aynı örneklerden oluşan tekrarları temizler

USE akilli_sehir_db;

-- Önce mevcut durumu kontrol et
SELECT 'Mevcut şikayet sayısı:' as Info, COUNT(*) as Count FROM Issues;
SELECT 'Mevcut başvuru sayısı:' as Info, COUNT(*) as Count FROM Applications;

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
-- Önce hangi başlıkların çok tekrar ettiğini bul ve sadece en yeni 3'ünü tut
SET @row_number = 0;
SET @prev_title = '';
SET @prev_user = 0;

DELETE FROM Issues
WHERE issue_id IN (
    SELECT issue_id FROM (
        SELECT 
            issue_id,
            @row_number := CASE 
                WHEN @prev_title = title AND @prev_user = user_id 
                THEN @row_number + 1 
                ELSE 1 
            END AS row_num,
            @prev_title := title,
            @prev_user := user_id
        FROM Issues
        ORDER BY title, user_id, created_at DESC, issue_id DESC
    ) AS ranked
    WHERE row_num > 3
);

-- 3. Tekrar eden başvuruları temizle (aynı project_id, user_id ve notes'a sahip olanlar)
DELETE a1 FROM Applications a1
INNER JOIN Applications a2 
WHERE a1.application_id < a2.application_id 
AND a1.project_id = a2.project_id 
AND a1.user_id = a2.user_id 
AND (a1.notes = a2.notes OR (a1.notes IS NULL AND a2.notes IS NULL));

-- Temizlik sonrası durumu kontrol et
SELECT 'Temizlik sonrası şikayet sayısı:' as Info, COUNT(*) as Count FROM Issues;
SELECT 'Temizlik sonrası başvuru sayısı:' as Info, COUNT(*) as Count FROM Applications;

-- Kalan şikayetleri göster (başlık ve sayıları)
SELECT title, COUNT(*) as count, GROUP_CONCAT(DISTINCT user_id) as user_ids
FROM Issues
GROUP BY title
ORDER BY count DESC
LIMIT 20;

-- Sonuçları göster
SELECT 'Temizlik işlemi tamamlandı!' as Message;

