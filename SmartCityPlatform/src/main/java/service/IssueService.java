package service;

import dao.IssueDAO;
import java.util.List;
import models.Issue;

public class IssueService {
   
    private IssueDAO issueDAO = new IssueDAO();

    // Vatandaşın şikayet oluşturma kuralı
    public void reportNewIssue(int userId, String title, String description) {
        reportNewIssue(userId, title, description, null);
    }
    
    public void reportNewIssue(int userId, String title, String description, Integer categoryId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Şikayet başlığı boş olamaz!");
        }

        Issue newIssue = new Issue();
        newIssue.setUserId(userId);
        newIssue.setTitle(title);
        newIssue.setDescription(description);
        newIssue.setCategoryId(categoryId);

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
        return issueDAO.getAllIssues(null, null);
    }
    
    /**
     * Admin için filtrelenmiş şikayetleri getirir
     * @param categoryId Kategori ID'si (null ise tüm kategoriler)
     * @param status Durum (null ise tüm durumlar)
     * @return Filtrelenmiş şikayetler listesi
     */
    public List<Issue> getAllIssuesForAdmin(Integer categoryId, String status) {
        return issueDAO.getAllIssues(categoryId, status);
    }
    
    /**
     * Kullanıcının tüm şikayetlerini getirir
     * 
     * @param userId Kullanıcı ID'si
     * @return Kullanıcının şikayetleri listesi
     */
    public List<Issue> getMyIssues(int userId) {
        return issueDAO.findAllByUserId(userId);
    }
    
    /**
     * Kategori başarı raporunu getirir (Stored Procedure kullanarak)
     * 
     * @return List of category report maps
     * @author Elif
     */
    public java.util.List<java.util.Map<String, Object>> getCategoryReport() {
        return issueDAO.getCategoryReport();
    }
    
    /**
     * Eski metod uyumluluk için (deprecated)
     * @deprecated Use getCategoryReport() instead
     */
    @Deprecated
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
     * Son 30 günün günlük istatistiklerini getirir (Stored Procedure kullanarak)
     * 
     * @return List of daily statistics maps
     * @author Elif
     */
    public java.util.List<java.util.Map<String, Object>> getMonthlyStats() {
        return issueDAO.getMonthlyStats();
    }
    
    /**
     * Eski metod uyumluluk için (deprecated)
     * @deprecated Use getMonthlyStats() instead
     */
    @Deprecated
    public void printMonthlyStats() {
        issueDAO.printMonthlyStats();
    }
    
    /**
     * Şikayet önceliğini güncelleme
     * 
     * @param issueId Şikayet ID'si
     * @param priority Öncelik seviyesi (Yüksek, Orta, Düşük)
     */
    public void updatePriority(int issueId, String priority) {
        if (priority == null || (!priority.equals("Yüksek") && !priority.equals("Orta") && !priority.equals("Düşük"))) {
            throw new IllegalArgumentException("Geçersiz öncelik seviyesi! (Yüksek, Orta, Düşük olmalı)");
        }
        
        issueDAO.updateIssuePriority(issueId, priority);
    }
}