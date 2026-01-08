package controller;

import models.Issue;
import models.User;
import models.Application;
import service.IssueService;
import dao.ProjectDAO;
import dao.ApplicationDAO;
import java.util.List;

/**
 * CitizenController - Vatandaş İşlemleri Controller'ı
 * 
 * Bu controller, vatandaşların yapabileceği işlemleri yönetir:
 * - Şikayet/talep oluşturma
 * - Açık projeleri görüntüleme
 * - Projelere başvurma
 * - Kendi başvurularını görüntüleme
 * 
 * Endpoint'ler:
 * - /issues/create
 * - /projects/view-open
 * - /projects/apply
 * - /applications/my
 * 
 * @author Esma
 * @version 1.0
 */
public class CitizenController {
    
    private IssueService issueService;
    private ProjectDAO projectDAO; // Şimdilik direkt DAO kullanıyoruz (ProjectService henüz mevcut değil)
    private ApplicationDAO applicationDAO; // Şimdilik direkt DAO kullanıyoruz
    
    public CitizenController() {
        this.issueService = new IssueService();
        this.projectDAO = new ProjectDAO();
        this.applicationDAO = new ApplicationDAO();
    }
    
    /**
     * Şikayet/Talep Oluşturma
     * Endpoint: /issues/create
     * 
     * @param userId Vatandaşın kullanıcı ID'si
     * @param title Şikayet başlığı
     * @param description Şikayet açıklaması
     * @return Başarılıysa true
     * @author Esma
     */
    public boolean createIssue(int userId, String title, String description) {
        // Validasyon işlemleri
        if (title == null || title.trim().isEmpty()) {
            System.out.println("❌ Hata: Şikayet başlığı boş olamaz!");
            return false;
        }
        
        if (description == null || description.trim().isEmpty()) {
            System.out.println("❌ Hata: Şikayet açıklaması boş olamaz!");
            return false;
        }
        
        // Service katmanını çağırarak şikayet kaydını oluşturuyoruz
        issueService.reportNewIssue(userId, title, description);
        System.out.println("✅ Şikayetiniz başarıyla kaydedildi!");
        return true;
    }
    
    /**
     * Kullanıcının Kendi Şikayetlerini Görüntüleme
     * Endpoint: /issues/my
     * 
     * @param userId Vatandaşın kullanıcı ID'si
     * @return Kullanıcının şikayetleri listesi
     * @author Esma
     */
    public List<Issue> getMyIssues(int userId) {
        // Service katmanından tüm şikayetleri alıp filtreliyoruz
        // Not: İdeal mimaride Service katmanında getMyIssues metodu olmalıdır
        List<Issue> allIssues = issueService.getAllIssuesForAdmin();
        
        // Kullanıcının kendi şikayetlerini filtreliyoruz
        List<Issue> myIssues = allIssues.stream()
            .filter(issue -> issue.getUserId() == userId)
            .collect(java.util.stream.Collectors.toList());
        
        if (myIssues.isEmpty()) {
            System.out.println("📭 Henüz şikayetiniz bulunmamaktadır.");
        } else {
            System.out.println("\n📋 Şikayetleriniz:");
            System.out.println("ID | Başlık | Durum");
            System.out.println("-------------------");
            for (Issue issue : myIssues) {
                System.out.println(issue.getIssueId() + " | " + issue.getTitle() + " | " + issue.getStatus());
            }
        }
        
        return myIssues;
    }
    
    /**
     * Açık Projeleri Görüntüleme
     * Endpoint: /projects/view-open
     * 
     * @return Açık projeler listesi
     * @author Esma
     */
    public List<String> viewOpenProjects() {
        List<String> openProjects = projectDAO.findAllOpen();
        
        if (openProjects.isEmpty()) {
            System.out.println("📭 Şu anda açık proje bulunmamaktadır.");
        } else {
            System.out.println("\n🏗️ Açık Belediye Projeleri:");
            for (int i = 0; i < openProjects.size(); i++) {
                System.out.println((i + 1) + ". " + openProjects.get(i));
            }
        }
        
        return openProjects;
    }
    
    /**
     * Projeye Başvurma
     * Endpoint: /projects/apply
     * 
     * @param projectId Başvurulacak proje ID'si
     * @param citizenId Vatandaşın kullanıcı ID'si
     * @param notes Başvuru notu (opsiyonel)
     * @return Başarılıysa true
     * @author Esma
     */
    public boolean applyForProject(int projectId, int citizenId, String notes) {
        // Validasyon işlemleri
        if (projectId <= 0) {
            System.out.println("❌ Hata: Geçersiz proje ID!");
            return false;
        }
        
        if (notes == null) {
            notes = ""; // Boş not kabul edilebilir
        }
        
        // DAO'yu çağırıyoruz (ApplicationService henüz mevcut değil)
        applicationDAO.save(projectId, citizenId, notes);
        System.out.println("✅ Başvurunuz alındı! Onay bekleniyor...");
        return true;
    }
    
    /**
     * Kullanıcının Kendi Başvurularını Görüntüleme
     * Endpoint: /applications/my
     * 
     * @param userId Vatandaşın kullanıcı ID'si
     * @author Esma
     */
    public void viewMyApplications(int userId) {
    System.out.println("📋 Başvurularınız görüntüleniyor...");
    applicationDAO.findByUserId(userId);
}
    
    /**
     * Kullanıcının duruma göre şikayetlerini görüntüleme (Stored Procedure kullanarak)
     * 
     * @param userId Kullanıcı ID'si
     * @param status Şikayet durumu (Yeni, İnceleniyor, Çözüldü)
     * @author Elif
     */
    public void viewMyIssuesByStatus(int userId, String status) {
        if (status == null || status.trim().isEmpty()) {
            System.out.println("❌ Hata: Durum belirtilmelidir!");
            return;
        }
        
        List<Issue> issues = issueService.getUserIssuesByStatus(userId, status);
        
        if (issues.isEmpty()) {
            System.out.println("📭 '" + status + "' durumunda şikayetiniz bulunmamaktadır.");
        } else {
            System.out.println("\n📋 '" + status + "' Durumundaki Şikayetleriniz:");
            System.out.println("ID | Başlık | Durum");
            System.out.println("-------------------");
            for (Issue issue : issues) {
                System.out.println(issue.getIssueId() + " | " + issue.getTitle() + " | " + issue.getStatus());
            }
        }
    }
}

