

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

**Kişi 1: Elif Altun** *Sorumluluk: Veritabanı Tasarımı, POJO Modelleri, DAO Katmanı, Service Arayüzleri.*

---
