# 🚀 Projeyi Çalıştırma Rehberi - Esma

Bu rehber, projeyi adım adım nasıl çalıştıracağınızı anlatır.

## 📋 Gereksinimler

1. **Java JDK** (Java 8 veya üzeri) - Kurulu olmalı
2. **MySQL** - Kurulu ve çalışıyor olmalı
3. **MySQL JDBC Driver** - Projede zaten var (mysql-connector-j-8.2.0.jar)

## 🔧 Adım 1: MySQL'in Çalıştığından Emin Olun

### XAMPP Kullanıyorsanız:
1. XAMPP Control Panel'i açın
2. **MySQL** yanındaki **Start** butonuna tıklayın
3. Yeşil renk olana kadar bekleyin

### WAMP Kullanıyorsanız:
1. WAMP ikonuna sağ tıklayın
2. **Start All Services** seçin
3. MySQL'in çalıştığından emin olun

### MySQL Workbench Kullanıyorsanız:
- MySQL Workbench'i açın ve bağlantınızın çalıştığını kontrol edin

## 🔑 Adım 2: MySQL Şifresini Ayarlayın

1. `SmartCityPlatform/src/util/DBConnection.java` dosyasını açın
2. 12. satırdaki şifre kısmını bulun:
   ```java
   private static final String PASSWORD = ""; // MySQL şifreni buraya yazmalısın
   ```
3. Şifreniz varsa tırnak işaretleri arasına yazın:
   - Şifre yoksa: `PASSWORD = ""` (boş bırakın)
   - Şifre varsa: `PASSWORD = "sifreniz"` (örnek: `PASSWORD = "admin"`)

4. Aynı şifreyi `DatabaseSetup.java` dosyasında da ayarlayın (18. satır)

## 🏗️ Adım 3: Projeyi Derleyin

Terminal/Command Prompt'u açın ve şu komutu çalıştırın:

```bash
cd SmartCityPlatform
javac -d bin -encoding UTF-8 -cp "src;mysql-connector-j-8.2.0.jar" src/**/*.java
```

**Not:** Windows'ta `;` kullanın, Linux/Mac'te `:` kullanın.

## ▶️ Adım 4: Projeyi Çalıştırın

```bash
java -cp "bin;mysql-connector-j-8.2.0.jar" Main
```

**Not:** Windows'ta `;` kullanın, Linux/Mac'te `:` kullanın.

## 🎯 İlk Çalıştırma

İlk çalıştırmada:
1. Proje otomatik olarak veritabanını oluşturacak
2. Tüm tabloları kuracak
3. Örnek verileri ekleyecek

## 👤 Test Kullanıcıları

Proje çalıştıktan sonra şu kullanıcılarla giriş yapabilirsiniz:

### Admin (Yönetici):
- **Kullanıcı Adı:** `admin_elif`
- **Şifre:** `123456`

### Vatandaş:
- **Kullanıcı Adı:** `vatandas_ali`
- **Şifre:** `654321`

vatandaş:  esma şifre:123456

## 📝 Kullanım

1. Program başladığında menü görünecek
2. **1** seçerek giriş yapabilir veya **2** seçerek yeni kayıt oluşturabilirsiniz
3. Giriş yaptıktan sonra rolünüze göre (Admin/Vatandaş) menü görünecek

## ❌ Sorun Giderme

### "Access denied" Hatası:
- MySQL şifrenizi kontrol edin
- `DBConnection.java` ve `DatabaseSetup.java` dosyalarındaki şifrelerin aynı olduğundan emin olun

### "Unknown database" Hatası:
- Proje otomatik olarak veritabanını oluşturmalı
- Eğer oluşturmazsa, `DatabaseSetup.java` dosyasını kontrol edin

### "ClassNotFoundException: com.mysql.cj.jdbc.Driver":
- `mysql-connector-j-8.2.0.jar` dosyasının proje klasöründe olduğundan emin olun
- Classpath'te JAR dosyasını belirttiğinizden emin olun

### MySQL Bağlantı Hatası:
- MySQL'in çalıştığından emin olun
- Port 3306'nın açık olduğundan emin olun
- Firewall'un MySQL'i engellemediğinden emin olun

## 💡 İpuçları

- Projeyi IDE'de (VS Code, IntelliJ, Eclipse) çalıştırırsanız daha kolay olur
- Her değişiklikten sonra projeyi yeniden derlemeyi unutmayın
- Veritabanı şifrenizi değiştirirseniz, her iki dosyada da güncellemeyi unutmayın

## 📞 Yardım

Sorun yaşarsanız:
1. Hata mesajını okuyun
2. Yukarıdaki sorun giderme bölümüne bakın
3. MySQL'in çalıştığından emin olun
4. Şifrelerin doğru olduğundan emin olun

---

**Hazırlayan: Esma** (Kişi 3 - Controller Katmanı)


