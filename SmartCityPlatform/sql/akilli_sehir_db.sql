/* Akıllı Şehir Geri Bildirim ve Yönetim Platformu - Veritabanı Şeması
   Kişi 1 Sorumluluğu: Elif Altun 
*/

-- 1. Veritabanını Oluşturma ve Seçme
CREATE DATABASE IF NOT EXISTS akilli_sehir_db;
USE akilli_sehir_db;

-- 2. Tabloların Oluşturulması (İlişki Sırasına Göre)

-- Kullanıcılar Tablosu
CREATE TABLE IF NOT EXISTS Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CITIZEN', 'ADMIN') NOT NULL,
    full_name VARCHAR(100)
);

-- Kategoriler Tablosu
CREATE TABLE IF NOT EXISTS Categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    responsible_unit VARCHAR(100)
);

-- Şikayetler Tablosu
CREATE TABLE IF NOT EXISTS Issues (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    category_id INT, -- Kategori ilişkisi eklendi
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status ENUM('Yeni', 'İnceleniyor', 'Çözüldü') DEFAULT 'Yeni',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);

-- Belediye Projeleri Tablosu
CREATE TABLE IF NOT EXISTS Projects (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    status ENUM('Açık', 'Kapalı', 'Tamamlandı') DEFAULT 'Açık',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Proje Başvuruları Tablosu
CREATE TABLE IF NOT EXISTS Applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT,
    user_id INT,
    application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Beklemede', 'Onaylandı', 'Reddedildi') DEFAULT 'Beklemede',
    notes TEXT,
    FOREIGN KEY (project_id) REFERENCES Projects(project_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- 3. Örnek Verilerin Eklenmesi (Test Verileri)

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

-- Şikayetler (Category_id değerleri ile)
INSERT INTO Issues (user_id, category_id, title, description, status) VALUES 
(1, 3, 'Sokak Lambası Arızası', 'Atatürk caddesi, No:45 önündeki lamba yanmıyor.', 'Yeni'),
(1, 3, 'Yol Çukuru Bildirimi', 'Okul yolunda derin bir çukur oluşmuş, tehlike arz ediyor.', 'İnceleniyor'),
(2, 2, 'Park Temizliği', 'Çocuk parkındaki oyun alanları çok kirli.', 'Çözüldü');

-- Başvurular
INSERT INTO Applications (project_id, user_id, notes, status) VALUES 
(1, 1, 'Gönüllü bisiklet sürüş eğitmeni olarak destek verebilirim.', 'Beklemede'),
(2, 2, 'Mahallemizdeki geri dönüşüm organizasyonu için gönüllüyüm.', 'Onaylandı');