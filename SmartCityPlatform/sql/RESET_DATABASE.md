# Veritabanını Sıfırdan Kurma Rehberi

Bu rehber, veritabanını tamamen temizleyip sıfırdan kurmanızı sağlar.

## Adım 1: Veritabanını Temizle

Terminal'de şu komutu çalıştırın:

```bash
cd ~/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/reset_database.sql
```

## Adım 2: Schema'yı Oluştur

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/schema.sql
```

## Adım 3: Priority Kolonunu Ekle

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/add_priority_column.sql
```

## Adım 4: Stored Procedure'ları Oluştur

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/procedures.sql
```

## Adım 5: Trigger'ları Oluştur

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/triggers.sql
```

## Adım 6: Örnek Verileri Ekle

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/seed_data.sql
```

## Tek Komutla Tümünü Çalıştırma

Eğer `reset_and_setup.sh` scriptini kullanmak isterseniz:

```bash
cd ~/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform
chmod +x sql/reset_and_setup.sh
./sql/reset_and_setup.sh
```

## Manuel Çalıştırma (Alternatif)

Eğer MySQL şifresini komut satırında göstermek istemiyorsanız:

```bash
mysql -u root -p akilli_sehir_db
```

Sonra MySQL prompt'unda sırasıyla:

```sql
source sql/reset_database.sql;
source sql/schema.sql;
source sql/add_priority_column.sql;
source sql/procedures.sql;
source sql/triggers.sql;
source sql/seed_data.sql;
exit;
```

## Kontrol

Veritabanının doğru kurulduğunu kontrol etmek için:

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db -e "SHOW TABLES;"
```

Şu tablolar görünmeli:
- Users
- Categories
- Issues
- Projects
- Applications

## Sorun Giderme

Eğer "Can't connect to local MySQL server" hatası alırsanız:

1. MySQL servisinin çalıştığından emin olun:
   ```bash
   # macOS
   brew services list
   # veya
   sudo /usr/local/mysql/support-files/mysql.server start
   ```

2. MySQL socket dosyasının konumunu kontrol edin:
   ```bash
   mysql_config --socket
   ```

3. Eğer socket farklı bir yerdeyse, bağlantıyı şu şekilde yapın:
   ```bash
   mysql -u root -p@Lifesk26 --socket=/var/run/mysqld/mysqld.sock akilli_sehir_db
   ```

