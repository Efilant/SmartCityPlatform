package controller;

import dao.ApplicationDAO;
import dao.ProjectDAO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Issue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ApplicationService;
import service.CategoryService;
import service.IssueService;

/**
 * AdminController - Yönetici İşlemleri REST Controller'ı
 * 
 * Bu controller, yöneticilerin yapabileceği işlemleri REST API olarak yönetir:
 * - Şikayetleri önceliklendirme/durum güncelleme
 * - Proje oluşturma
 * - Başvuruları onaylama/reddetme
 * - Analitik dashboard görüntüleme
 * 
 * REST Endpoint'ler:
 * - GET    /api/admin/issues
 * - PUT    /api/admin/issues/{issueId}/status
 * - PUT    /api/admin/issues/{issueId}/priority
 * - POST   /api/admin/projects
 * - PUT    /api/admin/projects/{projectId}/status
 * - GET    /api/admin/applications/project/{projectId}
 * - PUT    /api/admin/applications/{applicationId}/approve
 * - PUT    /api/admin/applications/{applicationId}/reject
 * - GET    /api/admin/dashboard/analytics
 * 
 * @author Esma
 * @version 2.0 (REST API)
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    private IssueService issueService;
    private ProjectDAO projectDAO;
    private ApplicationDAO applicationDAO;
    private CategoryService categoryService;
    private ApplicationService applicationService;
    
    public AdminController() {
        this.issueService = new IssueService();
        this.projectDAO = new ProjectDAO();
        this.applicationDAO = new ApplicationDAO();
        this.categoryService = new CategoryService();
        this.applicationService = new ApplicationService();
    }
    
    /**
     * Şikayet Durumunu Güncelleme
     * REST Endpoint: PUT /api/admin/issues/{issueId}/status
     * 
     * @param issueId Şikayet ID'si (path variable)
     * @param requestBody JSON body: {"status": "İnceleniyor" veya "Çözüldü"}
     * @return JSON response: {"success": true/false, "message": "..."}
     * @author Esma
     */
    @PutMapping("/issues/{issueId}/status")
    public ResponseEntity<Map<String, Object>> updateIssueStatus(
            @PathVariable int issueId,
            @RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        
        String newStatus = requestBody.get("status");
        
        // Validasyon işlemleri
        if (issueId <= 0) {
            response.put("success", false);
            response.put("message", "Geçersiz şikayet ID!");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (newStatus == null || (!newStatus.equals("İnceleniyor") && !newStatus.equals("Çözüldü"))) {
            response.put("success", false);
            response.put("message", "Geçersiz durum! (İnceleniyor veya Çözüldü olmalı)");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Service katmanını çağırarak durum güncellemesini yapıyoruz
        try {
            issueService.updateStatusByAdmin(issueId, newStatus);
            response.put("success", true);
            response.put("message", "Şikayet durumu başarıyla güncellendi!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Durum güncellenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Şikayet Önceliklendirme
     * REST Endpoint: PUT /api/admin/issues/{issueId}/priority
     * 
     * @param issueId Şikayet ID'si (path variable)
     * @param requestBody JSON body: {"priority": "Yüksek" veya "Orta" veya "Düşük"}
     * @return JSON response: {"success": true/false, "message": "..."}
     * @author Esma
     */
    @PutMapping("/issues/{issueId}/priority")
    public ResponseEntity<Map<String, Object>> prioritizeIssue(
            @PathVariable int issueId,
            @RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        
        String priority = requestBody.get("priority");
        
        // Validasyon işlemleri
        if (issueId <= 0) {
            response.put("success", false);
            response.put("message", "Geçersiz şikayet ID!");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (priority == null || (!priority.equals("Yüksek") && !priority.equals("Orta") && !priority.equals("Düşük"))) {
            response.put("success", false);
            response.put("message", "Geçersiz öncelik! (Yüksek, Orta, Düşük olmalı)");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            issueService.updatePriority(issueId, priority);
            response.put("success", true);
            response.put("message", "Şikayet #" + issueId + " için öncelik '" + priority + "' olarak işaretlendi.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Öncelik güncellenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Tüm Şikayetleri Listeleme (Kategori ve Durum Filtreli)
     * REST Endpoint: GET /api/admin/issues?categoryId=1&status=Yeni
     * 
     * @param categoryId Kategori ID'si (opsiyonel query parameter)
     * @param status Durum filtresi (opsiyonel query parameter: Yeni, İnceleniyor, Çözüldü)
     * @return JSON response: {"success": true, "issues": [...]}
     * @author Esma
     */
    @GetMapping("/issues")
    public ResponseEntity<Map<String, Object>> getAllIssues(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String status) {
        Map<String, Object> response = new HashMap<>();
        
        String filterStatus = null;
        if (status != null && !status.trim().isEmpty() && !status.equals("Tümü")) {
            filterStatus = status;
        }
        
        Integer filterCategoryId = null;
        if (categoryId != null && categoryId > 0) {
            filterCategoryId = categoryId;
        }
        
        List<Issue> issues = issueService.getAllIssuesForAdmin(filterCategoryId, filterStatus);
        
        response.put("success", true);
        response.put("issues", issues);
        response.put("count", issues.size());
        if (filterCategoryId != null) {
            response.put("categoryId", filterCategoryId);
        }
        if (filterStatus != null) {
            response.put("status", filterStatus);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Yeni Proje Oluşturma
     * REST Endpoint: POST /api/admin/projects
     * 
     * @param requestBody JSON body: {"title": "...", "description": "...", "startDate": "YYYY-MM-DD", "endDate": "YYYY-MM-DD"}
     * @return JSON response: {"success": true/false, "message": "..."}
     * @author Esma
     */
    @PostMapping("/projects")
    public ResponseEntity<Map<String, Object>> createProject(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        
        String title = requestBody.get("title");
        String description = requestBody.get("description");
        String startDate = requestBody.get("startDate");
        String endDate = requestBody.get("endDate");
        
        // Validasyon işlemleri
        if (title == null || title.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Proje başlığı boş olamaz!");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (description == null || description.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Proje açıklaması boş olamaz!");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Tarih validasyonu (basit kontrol)
        if (startDate == null || endDate == null) {
            response.put("success", false);
            response.put("message", "Başlangıç ve bitiş tarihleri belirtilmelidir!");
            return ResponseEntity.badRequest().body(response);
        }
        
        // DAO'yu çağırıyoruz (ProjectService henüz mevcut değil)
        try {
            projectDAO.save(title, description, startDate, endDate);
            response.put("success", true);
            response.put("message", "Proje başarıyla oluşturuldu!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Proje oluşturulurken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Proje Durumunu Güncelleme
     * REST Endpoint: PUT /api/admin/projects/{projectId}/status
     * 
     * @param projectId Proje ID'si (path variable)
     * @param requestBody JSON body: {"status": "Açık" veya "Kapalı" veya "Tamamlandı"}
     * @return JSON response: {"success": true/false, "message": "..."}
     * @author Esma
     */
    @PutMapping("/projects/{projectId}/status")
    public ResponseEntity<Map<String, Object>> updateProjectStatus(
            @PathVariable int projectId,
            @RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        
        String newStatus = requestBody.get("status");
        
        // Validasyon işlemleri
        if (projectId <= 0) {
            response.put("success", false);
            response.put("message", "Geçersiz proje ID!");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            projectDAO.updateStatus(projectId, newStatus);
            response.put("success", true);
            response.put("message", "Proje durumu başarıyla güncellendi!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Proje durumu güncellenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Proje Başvurularını Görüntüleme
     * REST Endpoint: GET /api/admin/applications/project/{projectId}
     * 
     * @param projectId Proje ID'si (path variable)
     * @return JSON response: {"success": true, "message": "..."}
     * @author Esma
     */
    @GetMapping("/applications/project/{projectId}")
    public ResponseEntity<Map<String, Object>> reviewApplications(@PathVariable int projectId) {
        Map<String, Object> response = new HashMap<>();
        
        // Validasyon işlemleri
        if (projectId <= 0) {
            response.put("success", false);
            response.put("message", "Geçersiz proje ID!");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // Stored Procedure kullanarak detaylı başvuru listesi
            List<Map<String, Object>> applications = applicationService.getProjectApplications(projectId);
            response.put("success", true);
            response.put("applications", applications);
            response.put("count", applications.size());
            response.put("projectId", projectId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Başvurular listelenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Başvuruyu Onaylama
     * REST Endpoint: PUT /api/admin/applications/{applicationId}/approve
     * 
     * @param applicationId Başvuru ID'si (path variable)
     * @return JSON response: {"success": true/false, "message": "..."}
     * @author Esma
     */
    @PutMapping("/applications/{applicationId}/approve")
    public ResponseEntity<Map<String, Object>> approveApplication(@PathVariable int applicationId) {
        Map<String, Object> response = new HashMap<>();
        
        // Validasyon işlemleri
        if (applicationId <= 0) {
            response.put("success", false);
            response.put("message", "Geçersiz başvuru ID!");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // DAO'yu çağırarak başvuru durumunu güncelliyoruz
            applicationDAO.updateStatus(applicationId, "Onaylandı");
            response.put("success", true);
            response.put("message", "Başvuru onaylandı!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Başvuru onaylanırken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Başvuruyu Reddetme
     * REST Endpoint: PUT /api/admin/applications/{applicationId}/reject
     * 
     * @param applicationId Başvuru ID'si (path variable)
     * @return JSON response: {"success": true/false, "message": "..."}
     * @author Esma
     */
    @PutMapping("/applications/{applicationId}/reject")
    public ResponseEntity<Map<String, Object>> rejectApplication(@PathVariable int applicationId) {
        Map<String, Object> response = new HashMap<>();
        
        // Validasyon işlemleri
        if (applicationId <= 0) {
            response.put("success", false);
            response.put("message", "Geçersiz başvuru ID!");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // DAO'yu çağırarak başvuru durumunu güncelliyoruz
            applicationDAO.updateStatus(applicationId, "Reddedildi");
            response.put("success", true);
            response.put("message", "Başvuru reddedildi.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Başvuru reddedilirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Analitik Dashboard Verilerini Görüntüleme
     * REST Endpoint: GET /api/admin/dashboard/analytics
     * 
     * Bu metod, yönetici için özet istatistikleri gösterir:
     * - Sistem genel istatistikleri (GetSystemStats procedure)
     * - Kategori başarı oranları (GetCategorySuccessRate procedure)
     * 
     * @return JSON response: {"success": true, "message": "..."}
     * @author Esma, Elif
     */
    @GetMapping("/dashboard/analytics")
    public ResponseEntity<Map<String, Object>> viewAnalyticsDashboard() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Sistem genel istatistikleri (Stored Procedure kullanarak)
            Map<String, Object> systemStats = projectDAO.getDashboardSummary();
            
            // Kategori başarı oranları (Stored Procedure kullanarak)
            List<Map<String, Object>> categoryReport = issueService.getCategoryReport();
            
            response.put("success", true);
            response.put("systemStats", systemStats);
            response.put("categoryReport", categoryReport);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Dashboard verileri görüntülenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Kategoriye göre bekleyen şikayetleri görüntüleme
     * REST Endpoint: GET /api/admin/issues/category/{categoryId}/pending
     * 
     * @param categoryId Kategori ID'si (path variable)
     * @return JSON response: {"success": true, "issues": [...]}
     * @author Elif
     */
    @GetMapping("/issues/category/{categoryId}/pending")
    public ResponseEntity<Map<String, Object>> viewPendingIssuesByCategory(@PathVariable int categoryId) {
        Map<String, Object> response = new HashMap<>();
        
        if (categoryId <= 0) {
            response.put("success", false);
            response.put("message", "Geçersiz kategori ID!");
            return ResponseEntity.badRequest().body(response);
        }
        
        List<Issue> issues = issueService.getPendingIssuesByCategory(categoryId);
        
        response.put("success", true);
        response.put("issues", issues);
        response.put("count", issues.size());
        response.put("categoryId", categoryId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * En çok şikayet alan kategorileri görüntüleme
     * REST Endpoint: GET /api/admin/categories/top/{limit}
     * 
     * @param limit Kaç kategori gösterilecek (path variable)
     * @return JSON response: {"success": true, "message": "..."}
     * @author Elif
     */
    @GetMapping("/categories/top/{limit}")
    public ResponseEntity<Map<String, Object>> viewTopCategories(@PathVariable int limit) {
        Map<String, Object> response = new HashMap<>();
        
        if (limit <= 0) {
            response.put("success", false);
            response.put("message", "Limit pozitif bir sayı olmalıdır!");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            List<Map<String, Object>> topCategories = categoryService.getTopCategories(limit);
            response.put("success", true);
            response.put("categories", topCategories);
            response.put("count", topCategories.size());
            response.put("limit", limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Kategoriler listelenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Son 30 günün istatistiklerini görüntüleme
     * REST Endpoint: GET /api/admin/stats/monthly
     * 
     * @return JSON response: {"success": true, "message": "..."}
     * @author Elif
     */
    @GetMapping("/stats/monthly")
    public ResponseEntity<Map<String, Object>> viewMonthlyStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> monthlyStats = issueService.getMonthlyStats();
            response.put("success", true);
            response.put("stats", monthlyStats);
            response.put("count", monthlyStats.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "İstatistikler görüntülenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

