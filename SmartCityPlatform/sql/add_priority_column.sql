-- Issue tablosuna priority kolonu ekleme
-- Bu dosya mevcut veritabanına priority özelliği ekler

USE akilli_sehir_db;

-- Priority kolonu ekle (eğer yoksa)
ALTER TABLE Issues 
ADD COLUMN IF NOT EXISTS priority ENUM('Yüksek', 'Orta', 'Düşük') DEFAULT 'Orta' AFTER status;

-- Mevcut kayıtlar için varsayılan öncelik atama
UPDATE Issues SET priority = 'Orta' WHERE priority IS NULL;

