# 🚀 Smart City Platform - Çalıştırma Talimatları

## ⚠️ ÖNEMLİ: Doğru Dizin

Proje **3 seviye iç içe klasör** yapısında bulunmaktadır:

```
Desktop/
└── SmartCityPlatform/
    └── SmartCityPlatform/
        └── SmartCityPlatform/  ← BURASI ANA PROJE KLASÖRÜ
            ├── pom.xml          ← Maven dosyası burada
            ├── src/
            └── ...
```

## 📍 Doğru Çalıştırma Komutu

**Terminal'de şu komutu kullanın:**

```bash
cd /Users/elifaltun/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform
mvn spring-boot:run
```

veya kısa yol:

```bash
cd ~/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform
mvn spring-boot:run
```

## ✅ Başarılı Çalıştırma Kontrolü

Uygulama başarıyla başladığında şu mesajları göreceksiniz:

```
========================================
  VERİTABANI KURULUMU KONTROL EDİLİYOR
========================================

✅ Veritabanı zaten mevcut: akilli_sehir_db
📋 SQL dosyaları çalıştırılıyor...

========================================
  REST API SUNUCUSU BAŞLATILIYOR
========================================

✅ REST API sunucusu başarıyla başlatıldı!
🌐 API Endpoint'leri: http://localhost:8080/api
```

## 🌐 Tarayıcıda Erişim

Uygulama başladıktan sonra:

1. **Giriş Sayfası:** http://localhost:8080/index.html
2. **API Endpoint'leri:** http://localhost:8080/api

## 🔧 Sorun Giderme

### Hata: "No POM in this directory"
**Çözüm:** `pom.xml` dosyasının bulunduğu dizine gidin:
```bash
cd ~/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform
```

### Hata: "Unable to find main class"
**Çözüm:** Projeyi temizleyip yeniden derleyin:
```bash
mvn clean compile
mvn spring-boot:run
```

### Hata: "MySQL connection failed"
**Çözüm:** 
1. MySQL servisinin çalıştığından emin olun
2. `src/main/resources/application.properties` dosyasındaki şifreyi kontrol edin

## 📝 Test Kullanıcıları

- **Admin:** `admin_elif` / `123456`
- **Vatandaş:** `vatandas_ali` / `654321`

## 🎯 Hızlı Başlangıç

```bash
# 1. Doğru dizine git
cd ~/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform

# 2. Uygulamayı başlat
mvn spring-boot:run

# 3. Tarayıcıda aç
# http://localhost:8080/index.html
```

