#!/bin/bash

# Tam Reset Scripti - Tüm Tabloları Sil ve Yeniden Oluştur
# Kullanım: ./full_reset.sh

DB_NAME="akilli_sehir_db"
DB_USER="root"
DB_PASSWORD="@Lifesk26"

echo "🗑️  Tüm tablolar siliniyor..."
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < sql/complete_reset.sql

echo "📋 Schema oluşturuluyor..."
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < sql/schema.sql

echo "📋 Stored procedure'lar oluşturuluyor..."
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < sql/procedures.sql

echo "📋 Trigger'lar oluşturuluyor..."
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < sql/triggers.sql

echo "🌱 Örnek veriler ekleniyor..."
mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < sql/seed_data.sql

echo "✅ Veritabanı başarıyla sıfırdan kuruldu!"

