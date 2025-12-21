package service;

import dao.IssueDAO;
import java.util.List;
import models.Issue;

public class IssueService {
   
    private IssueDAO issueDAO = new IssueDAO();

    // Vatandaşın şikayet oluşturma kuralı
  public void reportNewIssue(int userId, String title, String description) {
    if (title == null || title.trim().isEmpty()) {
        System.out.println("Hata: Şikayet başlığı boş olamaz! ❌");
        return;
    }

    Issue newIssue = new Issue();
    newIssue.setUserId(userId);
    newIssue.setTitle(title);
    newIssue.setDescription(description);

    issueDAO.createIssue(newIssue);
}

    // Admin'in durumu güncelleme kuralı
    public void updateStatusByAdmin(int issueId, String newStatus) {
        // İş kuralı: Sadece geçerli durumlar atanabilir
        if (newStatus.equals("İnceleniyor") || newStatus.equals("Çözüldü")) {
            issueDAO.updateIssueStatus(issueId, newStatus);
            
            if (newStatus.equals("Çözüldü")) {
                System.out.println("Vatandaşa bildirim gönderiliyor: Sorununuz çözüldü! 🔔");
            }
        } else {
            System.out.println("Geçersiz durum denemesi! ❌");
        }
    }

    public List<Issue> getAllIssuesForAdmin() {
        return issueDAO.getAllIssues();
    }
    
    /**
     * Kategori başarı raporunu gösterir (Stored Procedure kullanarak)
     * 
     * @author Elif
     */
    public void printCategoryReport() {
        issueDAO.printCategoryReport();
    }
    
    /**
     * Belirli bir kategorideki bekleyen şikayetleri getirir (Stored Procedure kullanarak)
     * 
     * @param categoryId Kategori ID'si
     * @return Bekleyen şikayetler listesi
     * @author Elif
     */
    public List<Issue> getPendingIssuesByCategory(int categoryId) {
        return issueDAO.getPendingIssuesByCategory(categoryId);
    }
    
    /**
     * Kullanıcının duruma göre şikayetlerini getirir (Stored Procedure kullanarak)
     * 
     * @param userId Kullanıcı ID'si
     * @param status Şikayet durumu (Yeni, İnceleniyor, Çözüldü)
     * @return Kullanıcının şikayetleri listesi
     * @author Elif
     */
    public List<Issue> getUserIssuesByStatus(int userId, String status) {
        return issueDAO.getUserIssuesByStatus(userId, status);
    }
    
    /**
     * Son 30 günün günlük istatistiklerini gösterir (Stored Procedure kullanarak)
     * 
     * @author Elif
     */
    public void printMonthlyStats() {
        issueDAO.printMonthlyStats();
    }
}