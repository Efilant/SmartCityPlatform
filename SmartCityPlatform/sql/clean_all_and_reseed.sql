-- TÜM VERİLERİ SİL VE YENİDEN EKLE
-- Bu script duplicate'leri tamamen temizler

USE akilli_sehir_db;

-- Foreign key constraint'leri devre dışı bırak
SET FOREIGN_KEY_CHECKS = 0;

-- TÜM VERİLERİ SİL
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

-- TEMİZ VERİLERİ EKLE

-- Kullanıcılar
INSERT INTO Users (username, password_hash, role, full_name) VALUES 
('admin_elif', '123456', 'ADMIN', 'Elif Admin'),
('vatandas_ali', '654321', 'CITIZEN', 'Ali Vatandas');

-- Kategoriler
INSERT INTO Categories (name, responsible_unit) VALUES 
('Ulaşım', 'Ulaşım Daire Başkanlığı'),
('Çevre ve Temizlik', 'Atık Yönetimi Birimi'),
('Altyapı', 'Fen İşleri Müdürlüğü'),
('Sosyal Hizmetler', 'Sosyal İşler Daire Başkanlığı');

-- Projeler
INSERT INTO Projects (title, description, start_date, end_date, status) VALUES 
('Akıllı Bisiklet Yolu', 'Şehir merkezine 10km kesintisiz bisiklet yolu yapımı.', '2024-01-01', '2024-06-01', 'Açık'),
('Sıfır Atık Kampanyası', 'Mahalle bazlı geri dönüşüm eğitimi ve kutu dağıtımı.', '2024-02-15', '2024-05-15', 'Açık'),
('Dijital Kütüphane', 'Gençler için 7/24 açık dijital çalışma ve internet alanı.', '2023-11-01', '2023-12-31', 'Kapalı');

-- Şikayetler (DUPLICATE YOK - Her biri benzersiz)
-- Priority kolonu varsa kullan, yoksa sadece temel alanları kullan
INSERT INTO Issues (user_id, category_id, title, description, status) VALUES 
(1, 3, 'Sokak Lambası Arızası', 'Atatürk caddesi, No:45 önündeki lamba yanmıyor.', 'Yeni'),
(1, 3, 'Yol Çukuru Bildirimi', 'Okul yolunda derin bir çukur oluşmuş, tehlike arz ediyor.', 'İnceleniyor'),
(2, 2, 'Park Temizliği', 'Çocuk parkındaki oyun alanları çok kirli.', 'Çözüldü');

-- Başvurular (Tüm başvurular varsayılan olarak "Beklemede" durumunda)
INSERT INTO Applications (project_id, user_id, notes, status) VALUES 
(1, 1, 'Gönüllü bisiklet sürüş eğitmeni olarak destek verebilirim.', 'Beklemede'),
(2, 2, 'Mahallemizdeki geri dönüşüm organizasyonu için gönüllüyüm.', 'Beklemede');

-- Kontrol: Kaç kayıt var?
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

-- Duplicate kontrolü
SELECT 'Duplicate kontrolü:' AS Info;
SELECT title, COUNT(*) as count 
FROM Issues 
GROUP BY title 
HAVING count > 1;

SELECT '✅ Temizleme ve yeniden ekleme tamamlandı!' AS Status;

