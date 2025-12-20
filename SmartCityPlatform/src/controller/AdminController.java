package controller;

import models.Issue;
import models.Project;
import service.IssueService;
import dao.ProjectDAO;
import dao.ApplicationDAO;
import java.util.List;

/**
 * AdminController - Yönetici İşlemleri Controller'ı
 * 
 * Bu controller, yöneticilerin yapabileceği işlemleri yönetir:
 * - Şikayetleri önceliklendirme/durum güncelleme
 * - Proje oluşturma
 * - Başvuruları onaylama/reddetme
 * - Analitik dashboard görüntüleme
 * 
 * Endpoint'ler:
 * - /issues/prioritize
 * - /issues/update-status
 * - /projects/create
 * - /applications/review
 * - /applications/approve
 * - /applications/reject
 * - /dashboard/analytics
 * 
 * @author Esma
 * @version 1.0
 */
public class AdminController {
    
    private IssueService issueService;
    private ProjectDAO projectDAO;
    private ApplicationDAO applicationDAO;
    
    public AdminController() {
        this.issueService = new IssueService();
        this.projectDAO = new ProjectDAO();
        this.applicationDAO = new ApplicationDAO();
    }
    
    /**
     * Şikayet Durumunu Güncelleme
     * Endpoint: /issues/update-status
     * 
     * @param issueId Şikayet ID'si
     * @param newStatus Yeni durum (İnceleniyor, Çözüldü)
     * @return Başarılıysa true
     * @author Esma
     */
    public boolean updateIssueStatus(int issueId, String newStatus) {
        // Validasyon işlemleri
        if (issueId <= 0) {
            System.out.println("❌ Hata: Geçersiz şikayet ID!");
            return false;
        }
        
        if (newStatus == null || (!newStatus.equals("İnceleniyor") && !newStatus.equals("Çözüldü"))) {
            System.out.println("❌ Hata: Geçersiz durum! (İnceleniyor veya Çözüldü olmalı)");
            return false;
        }
        
        // Service katmanını çağırarak durum güncellemesini yapıyoruz
        issueService.updateStatusByAdmin(issueId, newStatus);
        return true;
    }
    
    /**
     * Şikayet Önceliklendirme
     * Endpoint: /issues/prioritize
     * 
     * @param issueId Şikayet ID'si
     * @param priority Öncelik seviyesi (Yüksek, Orta, Düşük)
     * @return Başarılıysa true
     * @author Esma
     */
    public boolean prioritizeIssue(int issueId, String priority) {
        // Validasyon işlemleri
        if (issueId <= 0) {
            System.out.println("❌ Hata: Geçersiz şikayet ID!");
            return false;
        }
        
        // Not: Issue tablosunda priority kolonu yoksa, bu özellik için
        // veritabanı şemasına priority kolonu eklenmelidir.
        // Şimdilik sadece console çıktısı veriyoruz.
        System.out.println("📌 Şikayet #" + issueId + " için öncelik '" + priority + "' olarak işaretlendi.");
        System.out.println("(Not: Bu özellik için Issues tablosuna priority kolonu eklenmelidir)");
        return true;
    }
    
    /**
     * Tüm Şikayetleri Listeleme
     * Endpoint: /issues/all
     * 
     * @return Tüm şikayetler listesi
     * @author Esma
     */
    public List<Issue> getAllIssues() {
        List<Issue> issues = issueService.getAllIssuesForAdmin();
        
        if (issues.isEmpty()) {
            System.out.println("📭 Henüz şikayet bulunmamaktadır.");
        } else {
            System.out.println("\n📋 Tüm Şikayetler:");
            System.out.println("ID | Başlık | Durum | Açıklama");
            System.out.println("-----------------------------------");
            for (Issue issue : issues) {
                System.out.println(issue.getIssueId() + " | " + issue.getTitle() + 
                                 " | " + issue.getStatus() + " | " + issue.getDescription());
            }
        }
        
        return issues;
    }
    
    /**
     * Yeni Proje Oluşturma
     * Endpoint: /projects/create
     * 
     * @param title Proje başlığı
     * @param description Proje açıklaması
     * @param startDate Başlangıç tarihi (YYYY-MM-DD formatında)
     * @param endDate Bitiş tarihi (YYYY-MM-DD formatında)
     * @return Başarılıysa true
     * @author Esma
     */
    public boolean createProject(String title, String description, String startDate, String endDate) {
        // Validasyon işlemleri
        if (title == null || title.trim().isEmpty()) {
            System.out.println("❌ Hata: Proje başlığı boş olamaz!");
            return false;
        }
        
        if (description == null || description.trim().isEmpty()) {
            System.out.println("❌ Hata: Proje açıklaması boş olamaz!");
            return false;
        }
        
        // Tarih validasyonu (basit kontrol)
        if (startDate == null || endDate == null) {
            System.out.println("❌ Hata: Başlangıç ve bitiş tarihleri belirtilmelidir!");
            return false;
        }
        
        // DAO'yu çağırıyoruz (ProjectService henüz mevcut değil)
        projectDAO.save(title, description, startDate, endDate);
        return true;
    }
    
    /**
     * Proje Durumunu Güncelleme
     * Endpoint: /projects/update-status
     * 
     * @param projectId Proje ID'si
     * @param newStatus Yeni durum (Açık, Kapalı, Tamamlandı)
     * @return Başarılıysa true
     * @author Esma
     */
    public boolean updateProjectStatus(int projectId, String newStatus) {
        // Validasyon işlemleri
        if (projectId <= 0) {
            System.out.println("❌ Hata: Geçersiz proje ID!");
            return false;
        }
        
        projectDAO.updateStatus(projectId, newStatus);
        return true;
    }
    
    /**
     * Proje Başvurularını Görüntüleme
     * Endpoint: /applications/review
     * 
     * @param projectId Proje ID'si
     * @author Esma
     */
    public void reviewApplications(int projectId) {
        // Validasyon işlemleri
        if (projectId <= 0) {
            System.out.println("❌ Hata: Geçersiz proje ID!");
            return;
        }
        
        System.out.println("\n📋 Proje #" + projectId + " için Başvurular:");
        applicationDAO.findByProjectId(projectId);
    }
    
    /**
     * Başvuruyu Onaylama
     * Endpoint: /applications/approve
     * 
     * @param applicationId Başvuru ID'si
     * @return Başarılıysa true
     * @author Esma
     */
    public boolean approveApplication(int applicationId) {
        // Validasyon işlemleri
        if (applicationId <= 0) {
            System.out.println("❌ Hata: Geçersiz başvuru ID!");
            return false;
        }
        
        // DAO'yu çağırarak başvuru durumunu güncelliyoruz
        applicationDAO.updateStatus(applicationId, "Onaylandı");
        System.out.println("✅ Başvuru onaylandı!");
        return true;
    }
    
    /**
     * Başvuruyu Reddetme
     * Endpoint: /applications/reject
     * 
     * @param applicationId Başvuru ID'si
     * @return Başarılıysa true
     * @author Esma
     */
    public boolean rejectApplication(int applicationId) {
        // Validasyon işlemleri
        if (applicationId <= 0) {
            System.out.println("❌ Hata: Geçersiz başvuru ID!");
            return false;
        }
        
        // DAO'yu çağırarak başvuru durumunu güncelliyoruz
        applicationDAO.updateStatus(applicationId, "Reddedildi");
        System.out.println("❌ Başvuru reddedildi.");
        return true;
    }
    
    /**
     * Analitik Dashboard Verilerini Görüntüleme
     * Endpoint: /dashboard/analytics
     * 
     * Bu metod, yönetici için özet istatistikleri gösterir:
     * - Aktif proje sayısı
     * - Bekleyen başvuru sayısı
     * - vb.
     * 
     * @author Esma
     */
    public void viewAnalyticsDashboard() {
        System.out.println("\n📊 YÖNETİCİ ANALİTİK PANELİ");
        System.out.println("============================");
        projectDAO.printDashboardSummary();
    }
}

