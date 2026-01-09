#!/bin/bash

# Veritabanını Sıfırdan Kurma Scripti
# Kullanım: ./reset_and_setup.sh

DB_NAME="akilli_sehir_db"
DB_USER="root"
DB_PASSWORD="@Lifesk26"

echo "🗑️  Tüm veriler temizleniyor..."
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < sql/remove_all_duplicates.sql

echo "🌱 Örnek veriler ekleniyor..."
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < sql/seed_data.sql

echo "✅ Veritabanı başarıyla sıfırdan kuruldu!"

