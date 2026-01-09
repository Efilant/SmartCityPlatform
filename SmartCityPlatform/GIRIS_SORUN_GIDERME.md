# 🔧 Giriş Sorunu Giderme Rehberi

## ✅ Yapılan Düzeltmeler

1. **index.html'e api.js eklendi** - `auth.js` dosyası `api.js`'deki fonksiyonları kullanıyordu ama yüklenmemişti.

## 🔍 Sorun Tespiti Adımları

### 1. Backend'in Çalışıp Çalışmadığını Kontrol Edin

Terminal'de şu komutu çalıştırın:
```bash
cd ~/Desktop/SmartCityPlatform/SmartCityPlatform/SmartCityPlatform
mvn spring-boot:run
```

**Başarılı başlatma mesajları:**
```
✅ Veritabanı zaten mevcut: akilli_sehir_db
✅ REST API sunucusu başarıyla başlatıldı!
🌐 API Endpoint'leri: http://localhost:8080/api
```

### 2. Tarayıcı Console'unu Kontrol Edin

1. Tarayıcıda `http://localhost:8080/index.html` açın
2. F12 tuşuna basın (Developer Tools)
3. Console sekmesine gidin
4. Giriş yapmayı deneyin
5. Hata mesajlarını kontrol edin

**Olası hatalar:**
- `Failed to fetch` → Backend çalışmıyor
- `404 Not Found` → API endpoint yanlış
- `401 Unauthorized` → Kullanıcı adı/şifre yanlış
- `CORS error` → CORS ayarı eksik

### 3. Network Sekmesini Kontrol Edin

1. Developer Tools'da Network sekmesine gidin
2. Giriş yapmayı deneyin
3. `/api/auth/login` isteğini bulun
4. İsteğin durumunu kontrol edin:
   - **200 OK** → Başarılı
   - **401 Unauthorized** → Şifre yanlış
   - **500 Internal Server Error** → Backend hatası

### 4. Test Kullanıcılarını Kontrol Edin

Veritabanında kullanıcıların var olduğundan emin olun:

```sql
SELECT * FROM Users;
```

**Beklenen sonuç:**
- `admin_elif` / `123456` / `ADMIN`
- `vatandas_ali` / `654321` / `CITIZEN`

## 🐛 Yaygın Sorunlar ve Çözümleri

### Sorun 1: "Failed to fetch" Hatası

**Neden:** Backend çalışmıyor veya erişilemiyor

**Çözüm:**
1. Backend'in çalıştığından emin olun
2. Port 8080'in kullanılabilir olduğunu kontrol edin:
   ```bash
   lsof -i :8080
   ```
3. Firewall ayarlarını kontrol edin

### Sorun 2: "401 Unauthorized" Hatası

**Neden:** Kullanıcı adı veya şifre yanlış

**Çözüm:**
1. Test kullanıcılarını kullanın:
   - Admin: `admin_elif` / `123456`
   - Vatandaş: `vatandas_ali` / `654321`
2. Veritabanında şifrelerin düz metin olarak saklandığını unutmayın (hash değil)

### Sorun 3: "CORS" Hatası

**Neden:** Cross-Origin Resource Sharing ayarı eksik

**Çözüm:**
`AuthController.java` dosyasına CORS ayarı ekleyin:
```java
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // ...
}
```

### Sorun 4: Veritabanı Bağlantı Hatası

**Neden:** MySQL şifresi yanlış veya MySQL çalışmıyor

**Çözüm:**
1. MySQL'in çalıştığından emin olun
2. Şifreleri kontrol edin:
   - `src/main/java/util/DBConnection.java`
   - `src/main/java/util/DatabaseSetup.java`
   - `src/main/resources/application.properties`

## 📝 Manuel Test

### API'yi Manuel Test Edin

Terminal'de:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin_elif","password":"123456"}'
```

**Beklenen yanıt:**
```json
{
  "success": true,
  "message": "Giriş başarılı! Hoş geldiniz, Elif Admin",
  "user": {
    "userId": 1,
    "username": "admin_elif",
    "fullName": "Elif Admin",
    "role": "ADMIN"
  }
}
```

## 🎯 Hızlı Kontrol Listesi

- [ ] Backend çalışıyor mu? (`mvn spring-boot:run`)
- [ ] Port 8080 açık mı?
- [ ] MySQL çalışıyor mu?
- [ ] Veritabanı bağlantısı başarılı mı?
- [ ] Test kullanıcıları veritabanında var mı?
- [ ] Tarayıcı console'da hata var mı?
- [ ] Network sekmesinde istek başarılı mı?
- [ ] `api.js` ve `auth.js` dosyaları yüklendi mi?

## 💡 İletişim

Sorun devam ederse:
1. Tarayıcı console'daki hata mesajlarını paylaşın
2. Network sekmesindeki istek detaylarını paylaşın
3. Backend log'larını kontrol edin

