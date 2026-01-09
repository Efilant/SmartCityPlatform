# 🌐 Smart City Platform - REST API Dokümantasyonu

Bu dokümantasyon, Smart City Platform'un REST API endpoint'lerini ve kullanımlarını açıklar.

## 🚀 Başlatma

### Gereksinimler
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Spring Boot 3.2.0

### Kurulum ve Çalıştırma

1. **Veritabanı Ayarları**
   - `src/main/resources/application.properties` dosyasındaki MySQL şifresini güncelleyin
   - MySQL servisinin çalıştığından emin olun

2. **Projeyi Derleme**
   ```bash
   mvn clean install
   ```

3. **Uygulamayı Başlatma**
   ```bash
   mvn spring-boot:run
   ```
   veya
   ```bash
   java -jar target/smart-city-platform-1.0.0.jar
   ```

4. **API Erişimi**
   - Base URL: `http://localhost:8080/api`
   - Sunucu başarıyla başlatıldığında console'da endpoint listesi görüntülenir

## 📚 API Endpoint'leri

### 🔐 Kimlik Doğrulama (Authentication)

#### POST /api/auth/login
Kullanıcı girişi yapar.

**Request Body:**
```json
{
  "username": "kullanici_adi",
  "password": "sifre"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Giriş başarılı! Hoş geldiniz, ...",
  "user": {
    "userId": 1,
    "username": "kullanici_adi",
    "fullName": "Tam Ad",
    "role": "CITIZEN"
  }
}
```

**Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Giriş başarısız! Kullanıcı adı veya şifre hatalı."
}
```

#### POST /api/auth/register
Yeni kullanıcı kaydı oluşturur.

**Request Body:**
```json
{
  "username": "yeni_kullanici",
  "password": "sifre1234",
  "fullName": "Yeni Kullanıcı Adı"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Kayıt başarılı! Artık giriş yapabilirsiniz."
}
```

---

### 👤 Vatandaş İşlemleri (Citizen Operations)

#### POST /api/issues
Yeni şikayet/talep oluşturur.

**Request Body:**
```json
{
  "userId": 1,
  "title": "Şikayet Başlığı",
  "description": "Şikayet açıklaması detaylı olarak buraya yazılır."
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Şikayetiniz başarıyla kaydedildi!"
}
```

#### GET /api/issues/my/{userId}
Kullanıcının kendi şikayetlerini listeler.

**Response (200 OK):**
```json
{
  "success": true,
  "issues": [
    {
      "issueId": 1,
      "userId": 1,
      "title": "Şikayet Başlığı",
      "description": "Açıklama",
      "status": "Yeni"
    }
  ],
  "count": 1
}
```

#### GET /api/issues/my/{userId}/status/{status}
Kullanıcının belirli durumdaki şikayetlerini listeler.

**Path Parameters:**
- `userId`: Kullanıcı ID'si
- `status`: Şikayet durumu (Yeni, İnceleniyor, Çözüldü)

**Response (200 OK):**
```json
{
  "success": true,
  "issues": [...],
  "count": 0,
  "status": "Yeni"
}
```

#### GET /api/projects/open
Açık projeleri listeler.

**Response (200 OK):**
```json
{
  "success": true,
  "projects": ["Proje 1", "Proje 2"],
  "count": 2
}
```

#### POST /api/applications
Projeye başvuru yapar.

**Request Body:**
```json
{
  "projectId": 1,
  "userId": 1,
  "notes": "Başvuru notu (opsiyonel)"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Başvurunuz alındı! Onay bekleniyor..."
}
```

#### GET /api/applications/my/{userId}
Kullanıcının kendi başvurularını listeler.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Başvurularınız listelendi. (Detaylar console'da görüntülenir)"
}
```

---

### 👨‍💼 Yönetici İşlemleri (Admin Operations)

#### GET /api/admin/issues
Tüm şikayetleri listeler (sadece admin).

**Response (200 OK):**
```json
{
  "success": true,
  "issues": [...],
  "count": 10
}
```

#### PUT /api/admin/issues/{issueId}/status
Şikayet durumunu günceller.

**Request Body:**
```json
{
  "status": "İnceleniyor"
}
```

**Geçerli Durumlar:** `İnceleniyor`, `Çözüldü`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Şikayet durumu başarıyla güncellendi!"
}
```

#### PUT /api/admin/issues/{issueId}/priority
Şikayet önceliğini günceller.

**Request Body:**
```json
{
  "priority": "Yüksek"
}
```

**Geçerli Öncelikler:** `Yüksek`, `Orta`, `Düşük`

#### POST /api/admin/projects
Yeni proje oluşturur.

**Request Body:**
```json
{
  "title": "Proje Başlığı",
  "description": "Proje açıklaması",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Proje başarıyla oluşturuldu!"
}
```

#### PUT /api/admin/projects/{projectId}/status
Proje durumunu günceller.

**Request Body:**
```json
{
  "status": "Tamamlandı"
}
```

**Geçerli Durumlar:** `Açık`, `Kapalı`, `Tamamlandı`

#### GET /api/admin/applications/project/{projectId}
Belirli bir projeye yapılan başvuruları listeler.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Proje #1 için başvurular listelendi. (Detaylar console'da görüntülenir)"
}
```

#### PUT /api/admin/applications/{applicationId}/approve
Başvuruyu onaylar.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Başvuru onaylandı!"
}
```

#### PUT /api/admin/applications/{applicationId}/reject
Başvuruyu reddeder.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Başvuru reddedildi."
}
```

#### GET /api/admin/dashboard/analytics
Analitik dashboard verilerini görüntüler.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Analitik dashboard verileri görüntülendi. (Detaylar console'da görüntülenir)"
}
```

#### GET /api/admin/issues/category/{categoryId}/pending
Belirli kategorideki bekleyen şikayetleri listeler.

**Response (200 OK):**
```json
{
  "success": true,
  "issues": [...],
  "count": 5,
  "categoryId": 1
}
```

#### GET /api/admin/categories/top/{limit}
En çok şikayet alan kategorileri listeler.

**Path Parameters:**
- `limit`: Kaç kategori gösterilecek (örn: 5)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Top 5 kategori listelendi. (Detaylar console'da görüntülenir)"
}
```

#### GET /api/admin/stats/monthly
Son 30 günün istatistiklerini görüntüler.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Son 30 günün istatistikleri görüntülendi. (Detaylar console'da görüntülenir)"
}
```

---

## 🔧 Hata Yönetimi

Tüm endpoint'ler standart HTTP status kodlarını kullanır:

- **200 OK**: İstek başarılı
- **400 Bad Request**: Geçersiz istek parametreleri
- **401 Unauthorized**: Kimlik doğrulama başarısız
- **404 Not Found**: Kaynak bulunamadı
- **409 Conflict**: Çakışma (örn: kullanıcı adı zaten kullanılıyor)
- **500 Internal Server Error**: Sunucu hatası

Hata response formatı:
```json
{
  "success": false,
  "message": "Hata mesajı açıklaması"
}
```

## 📝 Notlar

1. **Console Çıktıları**: Bazı endpoint'ler (özellikle raporlama ve analitik) detaylı bilgileri console'a yazdırır. Bu endpoint'lerin response'larında sadece başarı mesajı döner.

2. **Veritabanı**: İlk çalıştırmada veritabanı otomatik olarak oluşturulur ve örnek veriler eklenir.

3. **Güvenlik**: Bu API şu anda authentication/authorization mekanizması içermemektedir. Production ortamında JWT veya Spring Security eklenmelidir.

4. **CORS**: Cross-Origin Resource Sharing (CORS) ayarları yapılmamıştır. Frontend entegrasyonu için CORS konfigürasyonu eklenmelidir.

## 🧪 Test Örnekleri

### cURL ile Test

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Şikayet Oluştur:**
```bash
curl -X POST http://localhost:8080/api/issues \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"title":"Test Şikayet","description":"Bu bir test şikayetidir"}'
```

**Tüm Şikayetleri Listele (Admin):**
```bash
curl -X GET http://localhost:8080/api/admin/issues
```

---

**Geliştirici Notları:**
- Tüm endpoint'ler RESTful prensiplere uygun olarak tasarlanmıştır
- JSON formatında request/response kullanılır
- HTTP metodları (GET, POST, PUT) REST standartlarına uygundur

