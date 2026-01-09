# Veritabanı Temizleme - Basit Yöntem

MySQL şifre sorunu yaşıyorsanız, bu basit yöntemleri kullanabilirsiniz.

## Şifre: `@Lifesk26`

Projede kullanılan MySQL şifresi: **@Lifesk26**

## Yöntem 1: Şifreyi doğrudan komutta belirtme (Önerilen)

```bash
cd ~/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/cleanup_duplicate_data.sql
```

**Not**: `-p` ile şifre arasında **boşluk olmamalı**!

## Yöntem 2: MySQL'e bağlanıp scripti çalıştırma

```bash
mysql -u root -p@Lifesk26
```

Sonra MySQL içinde:

```sql
USE akilli_sehir_db;
SOURCE sql/cleanup_duplicate_data.sql;
```

## Yöntem 3: MySQL Workbench kullanma (En Kolay)

1. MySQL Workbench'i açın
2. `akilli_sehir_db` veritabanına bağlanın (şifre: `@Lifesk26`)
3. File → Open SQL Script → `sql/cleanup_duplicate_data.sql` dosyasını seçin
4. Execute butonuna tıklayın (⚡ simgesi)

## Yöntem 4: phpMyAdmin kullanma

1. phpMyAdmin'i açın (genellikle `http://localhost/phpmyadmin`)
2. `akilli_sehir_db` veritabanını seçin
3. SQL sekmesine gidin
4. `sql/cleanup_duplicate_data.sql` dosyasının içeriğini yapıştırın
5. Go butonuna tıklayın

## Yöntem 5: Basitleştirilmiş script kullanma

Eğer yukarıdaki script çalışmazsa, basitleştirilmiş versiyonu deneyin:

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < sql/cleanup_duplicate_data_simple.sql
```

## Sorun Giderme

### Şifre hala çalışmıyorsa:

1. MySQL şifresini kontrol edin:
   ```bash
   mysql -u root -p
   ```
   Şifreyi manuel olarak girin

2. MySQL servisinin çalıştığından emin olun:
   ```bash
   # macOS
   brew services list | grep mysql
   
   # Veya
   sudo /usr/local/mysql/support-files/mysql.server status
   ```

3. MySQL'i yeniden başlatın:
   ```bash
   # macOS
   brew services restart mysql
   ```

## Yedek Alma (Önemli!)

Script'i çalıştırmadan önce mutlaka yedek alın:

```bash
mysqldump -u root -p@Lifesk26 akilli_sehir_db > backup_$(date +%Y%m%d_%H%M%S).sql
```

Yedeği geri yüklemek için:

```bash
mysql -u root -p@Lifesk26 akilli_sehir_db < backup_YYYYMMDD_HHMMSS.sql
```

