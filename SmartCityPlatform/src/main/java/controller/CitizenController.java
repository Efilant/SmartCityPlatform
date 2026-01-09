package controller;

import models.Issue;
import models.User;
import models.Application;
import models.Project;
import service.IssueService;
import dao.ProjectDAO;
import dao.ApplicationDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CitizenController - Vatandaş İşlemleri REST Controller'ı
 * 
 * Bu controller, vatandaşların yapabileceği işlemleri REST API olarak yönetir:
 * - Şikayet/talep oluşturma
 * - Açık projeleri görüntüleme
 * - Projelere başvurma
 * - Kendi başvurularını görüntüleme
 * 
 * REST Endpoint'ler:
 * - POST /api/issues
 * - GET  /api/issues/my/{userId}
 * - GET  /api/projects/open
 * - POST /api/applications
 * - GET  /api/applications/my/{userId}
 * 
 * @author Esma
 * @version 2.0 (REST API)
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
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
     * REST Endpoint: POST /api/issues
     * 
     * @param requestBody JSON body: {"userId": 1, "title": "...", "description": "...", "categoryId": 1}
     * @return JSON response: {"success": true/false, "message": "..."}
     * @author Esma
     */
    @PostMapping("/issues")
    public ResponseEntity<Map<String, Object>> createIssue(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> response = new HashMap<>();
        
        Integer userIdObj = (Integer) requestBody.get("userId");
        String title = (String) requestBody.get("title");
        String description = (String) requestBody.get("description");
        Integer categoryIdObj = requestBody.get("categoryId") != null ? 
            (Integer) requestBody.get("categoryId") : null;
        
        if (userIdObj == null) {
            response.put("success", false);
            response.put("message", "Kullanıcı ID'si gerekli!");
            return ResponseEntity.badRequest().body(response);
        }
        
        int userId = userIdObj;
        Integer categoryId = categoryIdObj;
        
        // Validasyon işlemleri
        if (title == null || title.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Şikayet başlığı boş olamaz!");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (description == null || description.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Şikayet açıklaması boş olamaz!");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Service katmanını çağırarak şikayet kaydını oluşturuyoruz
        try {
            issueService.reportNewIssue(userId, title, description, categoryId);
            response.put("success", true);
            response.put("message", "Şikayetiniz başarıyla kaydedildi!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Şikayet oluşturulurken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Kullanıcının Kendi Şikayetlerini Görüntüleme
     * REST Endpoint: GET /api/issues/my/{userId}
     * 
     * @param userId Vatandaşın kullanıcı ID'si (path variable)
     * @return JSON response: {"success": true, "issues": [...]}
     * @author Esma
     */
    @GetMapping("/issues/my/{userId}")
    public ResponseEntity<Map<String, Object>> getMyIssues(@PathVariable int userId) {
        Map<String, Object> response = new HashMap<>();
        
        // Service katmanından kullanıcının şikayetlerini alıyoruz
        List<Issue> myIssues = issueService.getMyIssues(userId);
        
        response.put("success", true);
        response.put("issues", myIssues);
        response.put("count", myIssues.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Açık Projeleri Görüntüleme
     * REST Endpoint: GET /api/projects/open
     * 
     * @return JSON response: {"success": true, "projects": [...]}
     * @author Esma
     */
    @GetMapping("/projects/open")
    public ResponseEntity<Map<String, Object>> viewOpenProjects() {
        Map<String, Object> response = new HashMap<>();
        
        List<Project> openProjects = projectDAO.findAllOpen();
        
        response.put("success", true);
        response.put("projects", openProjects);
        response.put("count", openProjects.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Projeye Başvurma
     * REST Endpoint: POST /api/applications
     * 
     * @param requestBody JSON body: {"projectId": 1, "userId": 1, "notes": "..."}
     * @return JSON response: {"success": true/false, "message": "..."}
     * @author Esma
     */
    @PostMapping("/applications")
    public ResponseEntity<Map<String, Object>> applyForProject(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> response = new HashMap<>();
        
        Integer projectIdObj = (Integer) requestBody.get("projectId");
        Integer userIdObj = (Integer) requestBody.get("userId");
        String notes = (String) requestBody.get("notes");
        
        if (projectIdObj == null || projectIdObj <= 0) {
            response.put("success", false);
            response.put("message", "Geçersiz proje ID!");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (userIdObj == null) {
            response.put("success", false);
            response.put("message", "Kullanıcı ID'si gerekli!");
            return ResponseEntity.badRequest().body(response);
        }
        
        int projectId = projectIdObj;
        int userId = userIdObj;
        
        if (notes == null) {
            notes = ""; // Boş not kabul edilebilir
        }
        
        // DAO'yu çağırıyoruz (ApplicationService henüz mevcut değil)
        try {
            applicationDAO.save(projectId, userId, notes);
            response.put("success", true);
            response.put("message", "Başvurunuz alındı! Onay bekleniyor...");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Başvuru oluşturulurken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Kullanıcının Kendi Başvurularını Görüntüleme
     * REST Endpoint: GET /api/applications/my/{userId}
     * 
     * @param userId Vatandaşın kullanıcı ID'si (path variable)
     * @return JSON response: {"success": true, "message": "Başvurular listelendi"}
     * @author Esma
     */
    @GetMapping("/applications/my/{userId}")
    public ResponseEntity<Map<String, Object>> viewMyApplications(@PathVariable int userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> applications = applicationDAO.findByUserIdWithProjectInfo(userId);
            
            response.put("success", true);
            response.put("applications", applications);
            response.put("count", applications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Başvurular listelenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Kullanıcının duruma göre şikayetlerini görüntüleme (Stored Procedure kullanarak)
     * REST Endpoint: GET /api/issues/my/{userId}/status/{status}
     * 
     * @param userId Kullanıcı ID'si (path variable)
     * @param status Şikayet durumu (Yeni, İnceleniyor, Çözüldü) (path variable)
     * @return JSON response: {"success": true, "issues": [...]}
     * @author Elif
     */
    @GetMapping("/issues/my/{userId}/status/{status}")
    public ResponseEntity<Map<String, Object>> viewMyIssuesByStatus(
            @PathVariable int userId, 
            @PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        
        if (status == null || status.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Durum belirtilmelidir!");
            return ResponseEntity.badRequest().body(response);
        }
        
        List<Issue> issues = issueService.getUserIssuesByStatus(userId, status);
        
        response.put("success", true);
        response.put("issues", issues);
        response.put("count", issues.size());
        response.put("status", status);
        
        return ResponseEntity.ok(response);
    }
}

