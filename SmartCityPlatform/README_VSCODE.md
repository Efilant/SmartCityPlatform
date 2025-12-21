# VS Code'da Projeyi Çalıştırma Rehberi

## 📋 Gereksinimler

1. **Java Extension Pack** - VS Code'da yüklü olmalı
2. **MySQL** - Çalışıyor olmalı
3. **MySQL Connector JAR** - İndirilip `lib/` klasörüne konulmalı

## 🔧 Kurulum Adımları

### 1. Java Extension Pack Kurulumu

VS Code'da:
1. Extensions sekmesine gidin (Ctrl+Shift+X / Cmd+Shift+X)
2. "Java Extension Pack" arayın
3. Microsoft tarafından sağlanan paketi yükleyin

### 2. MySQL Connector JAR Dosyasını İndirin

1. MySQL Connector/J indirme sayfasına gidin:
   https://dev.mysql.com/downloads/connector/j/

2. Platform Independent (ZIP) versiyonunu indirin

3. İndirdiğiniz ZIP dosyasından `mysql-connector-j-8.2.0.jar` dosyasını çıkarın

4. Bu dosyayı proje klasöründeki `lib/` klasörüne koyun:
   ```
   SmartCityPlatform/lib/mysql-connector-j-8.2.0.jar
   ```

### 3. VS Code'da Projeyi Açın

1. VS Code'u açın
2. File > Open Folder
3. `SmartCityPlatform` klasörünü seçin

## ▶️ Çalıştırma

### Yöntem 1: Run and Debug (Önerilen)

1. `Main.java` dosyasını açın
2. F5 tuşuna basın veya sol taraftaki Run and Debug ikonuna tıklayın
3. "Run Main" veya "Run Main (External Terminal)" seçeneğini seçin

### Yöntem 2: Terminal'den

1. Terminal'i açın (Ctrl+` / Cmd+`)
2. Şu komutu çalıştırın:
   ```bash
   javac -d bin -encoding UTF-8 -cp "src:lib/mysql-connector-j-8.2.0.jar" src/**/*.java
   java -cp "bin:lib/mysql-connector-j-8.2.0.jar" Main
   ```

### Yöntem 3: Task Runner

1. Terminal > Run Task
2. "compile" seçeneğini seçin (derleme için)
3. "run" seçeneğini seçin (çalıştırma için)

## ⚙️ MySQL Ayarları

`src/util/DBConnection.java` dosyasında şifrenizi kontrol edin:
- Şifre: `admin` (varsayılan)
- Değiştirmek isterseniz dosyayı düzenleyin

## 🐛 Sorun Giderme

### "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
- MySQL Connector JAR dosyasının `lib/` klasöründe olduğundan emin olun
- `.vscode/launch.json` dosyasındaki classpath'i kontrol edin

### "Access denied for user"
- MySQL şifrenizi `DBConnection.java` ve `DatabaseSetup.java` dosyalarında kontrol edin
- MySQL'in çalıştığından emin olun

### Java bulunamıyor
- Java Extension Pack'in yüklü olduğundan emin olun
- VS Code'u yeniden başlatın

## 📝 Notlar

- İlk çalıştırmada veritabanı otomatik oluşturulacak
- Test kullanıcıları:
  - Admin: `admin_elif` / `123456`
  - Vatandaş: `vatandas_ali` / `654321`

