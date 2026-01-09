# 🌐 Frontend Kullanım Kılavuzu

Bu dokümantasyon, Smart City Platform'un web arayüzünün kullanımını açıklar.

## 🚀 Başlatma

### 1. Backend'i Başlatma

```bash
cd SmartCityPlatform
mvn spring-boot:run
```

Backend başarıyla başlatıldığında:
- API sunucusu: `http://localhost:8080`
- Frontend: `http://localhost:8080/index.html`

### 2. Veritabanı Güncellemesi (Priority Kolonu İçin)

Priority özelliğini kullanmak için veritabanına priority kolonu eklenmelidir:

```bash
mysql -u root -p akilli_sehir_db < sql/add_priority_column.sql
```

veya MySQL Workbench'te `sql/add_priority_column.sql` dosyasını çalıştırın.

## 📱 Kullanıcı Arayüzü

### Giriş/Kayıt Sayfası (`index.html`)

**Özellikler:**
- Kullanıcı girişi (Login)
- Yeni kullanıcı kaydı (Register)
- Rol seçimi (Vatandaş/Yönetici)

**Test Kullanıcıları:**
- Admin: `admin_elif` / `123456`
- Vatandaş: `vatandas_ali` / `654321`

### Vatandaş Paneli (`citizen-dashboard.html`)

**Özellikler:**

1. **Yeni Şikayet Oluşturma**
   - Başlık girişi
   - Kategori seçimi
   - Detaylı açıklama
   - Şikayet gönderme

2. **Şikayetlerim**
   - Kendi şikayetlerini görüntüleme
   - Durum filtreleme (Yeni, İnceleniyor, Çözüldü)
   - Şikayet detaylarını görüntüleme

3. **Açık Projeler**
   - Belediye projelerini görüntüleme
   - Projelere başvuru yapma

4. **Başvurularım**
   - Yaptığı başvuruları görüntüleme
   - Başvuru durumlarını takip etme

### Yönetici Paneli (`admin-dashboard.html`)

**Özellikler:**

1. **Analitik Dashboard**
   - Toplam şikayet sayısı
   - Aktif proje sayısı
   - Toplam başvuru sayısı
   - Bekleyen şikayetler

2. **Tüm Şikayetler**
   - Tüm şikayetleri görüntüleme
   - Kategori ve durum filtreleme
   - Şikayet durumu güncelleme
   - Şikayet önceliklendirme (Yüksek, Orta, Düşük)

3. **Yeni Proje Oluşturma**
   - Proje başlığı ve açıklaması
   - Başlangıç ve bitiş tarihleri
   - Proje oluşturma

4. **Proje Başvuruları**
   - Proje seçimi
   - Başvuruları görüntüleme
   - Başvuruları onaylama/reddetme

5. **Raporlar ve Analitik**
   - Kategori başarı raporu
   - En çok şikayet alan kategoriler
   - Aylık istatistikler

## 🎨 Arayüz Özellikleri

### Tasarım
- Modern ve responsive tasarım
- Gradient renkler ve kart tabanlı layout
- Mobil uyumlu (responsive)
- Kullanıcı dostu arayüz

### Renk Kodları
- **Durum Badge'leri:**
  - Yeni: Sarı
  - İnceleniyor: Mavi
  - Çözüldü: Yeşil
  - Beklemede: Sarı
  - Onaylandı: Yeşil
  - Reddedildi: Kırmızı

### Navigasyon
- Sidebar menü ile kolay navigasyon
- Aktif sayfa vurgulaması
- Hızlı erişim butonları

## 🔧 Teknik Detaylar

### API Entegrasyonu
- Tüm frontend işlemleri REST API üzerinden yapılır
- API base URL: `http://localhost:8080/api`
- JSON formatında request/response

### LocalStorage
- Kullanıcı bilgileri localStorage'da saklanır
- Oturum yönetimi için kullanılır
- Çıkış yapıldığında temizlenir

### Hata Yönetimi
- Kullanıcı dostu hata mesajları
- Başarı mesajları
- Otomatik mesaj kaybolma (5 saniye)

## 📝 Kullanım Senaryoları

### Senaryo 1: Vatandaş Şikayet Oluşturma
1. `index.html` sayfasından giriş yap
2. Vatandaş paneline yönlendirilir
3. "Yeni Şikayet" sekmesine git
4. Başlık, kategori ve açıklama gir
5. "Şikayeti Gönder" butonuna tıkla
6. Başarı mesajı görüntülenir

### Senaryo 2: Admin Şikayet Yönetimi
1. Admin olarak giriş yap
2. "Tüm Şikayetler" sekmesine git
3. Şikayetleri görüntüle
4. Durum dropdown'ından durum seç
5. Öncelik dropdown'ından öncelik seç
6. Değişiklikler otomatik kaydedilir

### Senaryo 3: Proje Başvurusu
1. Vatandaş olarak giriş yap
2. "Açık Projeler" sekmesine git
3. Bir proje seç
4. "Başvur" butonuna tıkla
5. Başvuru notu gir (opsiyonel)
6. Başvuru gönderilir

### Senaryo 4: Admin Proje Oluşturma
1. Admin olarak giriş yap
2. "Yeni Proje" sekmesine git
3. Proje bilgilerini gir
4. Tarihleri seç
5. "Proje Oluştur" butonuna tıkla

## 🐛 Sorun Giderme

### API Bağlantı Hatası
- Backend'in çalıştığından emin olun
- `http://localhost:8080/api` adresine erişilebilir olduğunu kontrol edin
- Browser console'da hata mesajlarını kontrol edin

### Kategori Listesi Yüklenmiyor
- Backend'de CategoryController'ın çalıştığından emin olun
- Veritabanında Categories tablosunun dolu olduğunu kontrol edin

### Şikayet Oluşturulamıyor
- Kullanıcı ID'sinin doğru olduğundan emin olun
- Kategori seçildiğinden emin olun
- Backend loglarını kontrol edin

## 📚 İlgili Dosyalar

- **Frontend:** `src/main/resources/static/`
- **HTML:** `index.html`, `citizen-dashboard.html`, `admin-dashboard.html`
- **CSS:** `css/style.css`
- **JavaScript:** `js/api.js`, `js/auth.js`, `js/citizen.js`, `js/admin.js`
- **Backend API:** `REST_API_DOCUMENTATION.md`

## 🎯 Gelecek Geliştirmeler

- [ ] Görev atama (Assignments) özelliği
- [ ] Bildirim sistemi
- [ ] Dosya yükleme (şikayet fotoğrafları)
- [ ] Harita entegrasyonu (konum bazlı şikayetler)
- [ ] Email bildirimleri
- [ ] Dashboard grafikleri (Chart.js entegrasyonu)

