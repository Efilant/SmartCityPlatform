# Veritabanı Temizleme Rehberi

Bu rehber, veritabanındaki tekrar eden örnek verileri temizlemek için hazırlanmıştır.

## Temizleme Scripti

`sql/cleanup_duplicate_data.sql` dosyası şu işlemleri yapar:

1. **Tekrar eden şikayetleri temizler**: Aynı başlık, açıklama ve kullanıcı ID'sine sahip şikayetlerden sadece en yenisini tutar
2. **Çok tekrar eden şikayetleri sınırlar**: Aynı başlığa sahip 3'ten fazla şikayet varsa, her birinden sadece en yeni 3'ünü tutar
3. **Tekrar eden başvuruları temizler**: Aynı proje, kullanıcı ve notlara sahip başvurulardan sadece en yenisini tutar

## Scripti Çalıştırma

### Yöntem 1: MySQL Komut Satırı

```bash
mysql -u root -p akilli_sehir_db < sql/cleanup_duplicate_data.sql
```

### Yöntem 2: MySQL Workbench veya phpMyAdmin

1. MySQL Workbench veya phpMyAdmin'i açın
2. `akilli_sehir_db` veritabanını seçin
3. `sql/cleanup_duplicate_data.sql` dosyasının içeriğini kopyalayın
4. SQL sorgusunu çalıştırın

### Yöntem 3: Terminal'den MySQL'e Bağlanarak

```bash
mysql -u root -p
```

Sonra MySQL içinde:

```sql
USE akilli_sehir_db;
SOURCE sql/cleanup_duplicate_data.sql;
```

## Önemli Notlar

⚠️ **DİKKAT**: Bu script veritabanındaki verileri kalıcı olarak siler. Çalıştırmadan önce:

1. **Yedek alın**: Veritabanınızın yedeğini alın
2. **Test edin**: Önce test ortamında deneyin
3. **Kontrol edin**: Script çalıştıktan sonra sonuçları kontrol edin

## Script Sonrası Kontrol

Script çalıştıktan sonra:

1. Şikayet sayısının azaldığını kontrol edin
2. Başvuru sayısının azaldığını kontrol edin
3. Admin dashboard'da şikayetlerin doğru göründüğünü kontrol edin

## Sorun Giderme

Eğer script çalışırken hata alırsanız:

1. MySQL kullanıcı izinlerini kontrol edin
2. Foreign key constraint hataları varsa, önce ilişkili tabloları kontrol edin
3. Script'i adım adım çalıştırarak hangi satırda hata aldığınızı bulun

## Yedek Alma

Veritabanı yedeği almak için:

```bash
mysqldump -u root -p akilli_sehir_db > backup_$(date +%Y%m%d_%H%M%S).sql
```

Yedeği geri yüklemek için:

```bash
mysql -u root -p akilli_sehir_db < backup_YYYYMMDD_HHMMSS.sql
```

