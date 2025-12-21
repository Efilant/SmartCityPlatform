-- Tabloların Oluşturulması (İlişki Sırasına Göre)

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
