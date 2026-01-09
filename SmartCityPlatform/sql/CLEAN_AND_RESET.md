# Duplicate'leri Temizleme ve Veritabanını Sıfırlama Rehberi

Bu rehber, duplicate verileri temizleyip veritabanını sıfırdan kurmanızı sağlar.

## Yöntem 1: Tüm Verileri Sil ve Yeniden Kur (Önerilen)

### Adım 1: Tüm Verileri Sil

```bash
cd ~/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/remove_all_duplicates.sql
```

### Adım 2: Seed Data'yı Ekle

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/seed_data.sql
```

## Yöntem 2: Tam Reset (Tablo Yapısını da Yeniden Oluştur)

### Adım 1: Tüm Tabloları Sil

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/complete_reset.sql
```

### Adım 2: Schema'yı Oluştur

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/schema.sql
```

### Adım 3: Stored Procedure'ları Oluştur

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/procedures.sql
```

### Adım 4: Trigger'ları Oluştur

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/triggers.sql
```

### Adım 5: Seed Data'yı Ekle

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/seed_data.sql
```

## Yöntem 3: Tek Komutla (Script)

```bash
cd ~/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform
chmod +x sql/reset_and_setup.sh
./sql/reset_and_setup.sh
```

## Kontrol

Veritabanının doğru kurulduğunu kontrol etmek için:

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db -e "
SELECT 'Users' AS TableName, COUNT(*) AS Count FROM Users
UNION ALL
SELECT 'Categories', COUNT(*) FROM Categories
UNION ALL
SELECT 'Issues', COUNT(*) FROM Issues
UNION ALL
SELECT 'Projects', COUNT(*) FROM Projects
UNION ALL
SELECT 'Applications', COUNT(*) FROM Applications;
"
```

**Beklenen Sonuç:**
- Users: 2 (admin_elif, vatandas_ali)
- Categories: 4
- Issues: 3
- Projects: 3
- Applications: 2

## Duplicate Kontrolü

Eğer hala duplicate'ler varsa:

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db -e "
SELECT title, COUNT(*) as count 
FROM Issues 
GROUP BY title 
HAVING count > 1;
"
```

Bu sorgu duplicate şikayetleri gösterir. Eğer sonuç varsa, `remove_all_duplicates.sql` scriptini tekrar çalıştırın.

## Sorun Giderme

### MySQL Bağlantı Hatası

Eğer "Can't connect to local MySQL server" hatası alırsanız:

1. MySQL servisinin çalıştığından emin olun:
   ```bash
   # macOS
   brew services list
   # veya
   sudo /usr/local/mysql/support-files/mysql.server start
   ```

2. Socket dosyasını kontrol edin:
   ```bash
   mysql_config --socket
   ```

### Foreign Key Hatası

Eğer foreign key hatası alırsanız, script'te `SET FOREIGN_KEY_CHECKS = 0;` zaten var. Eğer hala sorun varsa:

```sql
SET FOREIGN_KEY_CHECKS = 0;
-- Silme işlemleri
SET FOREIGN_KEY_CHECKS = 1;
```

