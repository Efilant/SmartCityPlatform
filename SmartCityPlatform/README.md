

# 🏙️ Akıllı Şehir Geri Bildirim ve Yönetim Platformu

Bu platform, vatandaşların şehirdeki sorunları bildirmesini, projeleri takip etmesini ve gönüllü olarak başvuruda bulunmasını sağlayan **3 Katmanlı Mimari (DAO-Service-Controller)** yapısına sahip bir Java uygulamasıdır.

## 🛠️ Kişi 1 Sorumluluk Alanı: Veri Erişimi ve Temel Yapı

Sistemin veritabanı mimarisi, veri modelleri (POJO) ve veritabanı ile doğrudan iletişim kuran DAO katmanı tarafımdan geliştirilmiştir.

### 1. Veritabanı Mimarisi (MySQL)

Sistem, verimlilik ve bütünlük için 5 ana tablo üzerine kurgulanmıştır:

* **Users:** Kullanıcı bilgileri ve rollerin (Citizen/Admin) yönetimi.
* **Categories:** Şikayet ve projelerin birim bazlı (Ulaşım, Çevre vb.) sınıflandırılması.
* **Issues:** Vatandaşların ilettiği şikayet ve taleplerin kaydı.
* **Projects:** Belediye tarafından açılan aktif görevler ve projeler.
* **Applications:** Vatandaşların projelere yaptığı başvuruların takibi.

### 2. Veri Erişim Nesneleri (DAO) Metot Listesi

Tüm DAO sınıfları, dokümanda belirtilen temel metot imzalarını ve ek analitik sorguları içermektedir:

* **UserDAO:** `findById()`, `findByUsername()`, `save()`.
* **IssueDAO:** `save()`, `updateStatus()`, `findAllByUserId()`, `findAll()` ve **Kategori Bazlı Raporlama**.
* **ProjectDAO:** `save()`, `findAllOpen()`, `updateStatus()` ve **Yönetici Dashboard Özeti**.
* **ApplicationDAO:** `save()`, `findByProjectId()`, `updateStatus()`.
* **CategoryDAO:** `findAll()`, `findById()` (Sistemin sınıflandırma altyapısını yönetir).

### 3. Tanımlanan Servis Arayüzleri (Interfaces)

Kişi 2'nin iş mantığını (Service Layer) geliştirebilmesi için gerekli olan tüm "Service Interface" yapıları tanımlanmıştır:

* `IUserService`, `IIssueService`, `IProjectService`, `IApplicationService`.

---

## 🚀 Kurulum ve Başlatma

1. **Veritabanı:** Proje ana dizinindeki SQL scriptini MySQL Workbench üzerinde çalıştırarak `akilli_sehir_db` şemasını oluşturun.
2. **Bağlantı Ayarları:** `src/util/DBConnection.java` dosyasındaki `PASSWORD` alanını kendi yerel veritabanı şifrenizle güncelleyin.
3. **Sürücü:** `mysql-connector-j` kütüphanesinin projenin **Referenced Libraries** kısmına eklendiğinden emin olun.

---

## 📊 Raporlama Özellikleri (Faz III Geliştirmeleri)

Yönetici paneli için hazırlanan özel DAO sorguları sayesinde şu veriler anlık olarak izlenebilir:

* Kategori başına düşen toplam şikayet sayısı.
* Onay bekleyen toplam başvuru ve aktif proje sayısı.

---

## 🎮 Kişi 3 Sorumluluk Alanı: Controller/Presentation Katmanı

Controller katmanı, kullanıcı isteklerini yöneten ve Service katmanını çağıran arayüz katmanıdır. Tüm Controller sınıfları **Esma** tarafından geliştirilmiştir.

### 1. Controller Sınıfları

#### AuthController (Kimlik Doğrulama)
Kullanıcı girişi ve kayıt işlemlerini yönetir:
- **`login()`** - Endpoint: `/login` - Kullanıcı girişi ve doğrulama
- **`register()`** - Endpoint: `/register` - Yeni kullanıcı kaydı
- Validasyon işlemleri (boş alan kontrolü, şifre uzunluk kontrolü)

#### CitizenController (Vatandaş İşlemleri)
Vatandaşların yapabileceği tüm işlemleri yönetir:
- **`createIssue()`** - Endpoint: `/issues/create` - Şikayet/talep oluşturma
- **`getMyIssues()`** - Endpoint: `/issues/my` - Kendi şikayetlerini görüntüleme
- **`viewOpenProjects()`** - Endpoint: `/projects/view-open` - Açık projeleri listeleme
- **`applyForProject()`** - Endpoint: `/projects/apply` - Projeye başvurma
- **`viewMyApplications()`** - Endpoint: `/applications/my` - Başvurularını görüntüleme

#### AdminController (Yönetici İşlemleri)
Yöneticilerin yapabileceği tüm işlemleri yönetir:
- **`getAllIssues()`** - Endpoint: `/issues/all` - Tüm şikayetleri listeleme
- **`updateIssueStatus()`** - Endpoint: `/issues/update-status` - Şikayet durumu güncelleme
- **`prioritizeIssue()`** - Endpoint: `/issues/prioritize` - Şikayet önceliklendirme
- **`createProject()`** - Endpoint: `/projects/create` - Yeni proje oluşturma
- **`updateProjectStatus()`** - Endpoint: `/projects/update-status` - Proje durumu güncelleme
- **`reviewApplications()`** - Endpoint: `/applications/review` - Başvuruları görüntüleme
- **`approveApplication()`** - Endpoint: `/applications/approve` - Başvuruyu onaylama
- **`rejectApplication()`** - Endpoint: `/applications/reject` - Başvuruyu reddetme
- **`viewAnalyticsDashboard()`** - Endpoint: `/dashboard/analytics` - Analitik panel görüntüleme

### 2. Main.java Entegrasyonu

Ana uygulama sınıfı (`Main.java`) Controller katmanını kullanacak şekilde güncellenmiştir:
- Controller'lar üzerinden tüm işlemler yönetiliyor
- Kullanıcı menüleri Controller metodları ile çalışıyor
- Kayıt (register) özelliği eklendi
- Hata yönetimi iyileştirildi

### 3. Veritabanı Otomatik Kurulumu

`DatabaseSetup.java` sınıfı eklendi:
- İlk çalıştırmada veritabanını otomatik oluşturur
- Tüm tabloları kurar
- Örnek verileri ekler
- Veritabanı yoksa oluşturur, varsa mevcut yapıyı korur

### 4. Mimari Yapı

```
Main (Kullanıcı Arayüzü)
    ↓
Controller (İstek Yönetimi + Validasyon) ← Kişi 3 Sorumluluğu
    ↓
Service (İş Mantığı) ← Kişi 2 Sorumluluğu
    ↓
DAO (Veritabanı İşlemleri) ← Kişi 1 Sorumluluğu
    ↓
MySQL Veritabanı
```

### 5. Özellikler

- ✅ Tüm Controller sınıfları Türkçe yorumlarla dokümante edildi
- ✅ Her metoda `@author Esma` etiketi eklendi
- ✅ Validasyon işlemleri Controller katmanında yapılıyor
- ✅ Hata yönetimi ve kullanıcı geri bildirimleri eklendi
- ✅ Proje çalışır durumda ve test edildi

---

**Kişi 1: Elif Altun** *Sorumluluk: Veritabanı Tasarımı, POJO Modelleri, DAO Katmanı, Service Arayüzleri.*

**Kişi 3: Esma** *Sorumluluk: Controller/Presentation Katmanı, Kullanıcı Arayüzü Entegrasyonu, Veritabanı Otomatik Kurulumu.*

---
