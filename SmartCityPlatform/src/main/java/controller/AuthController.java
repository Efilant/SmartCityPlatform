package controller;

import dao.UserDAO;
import java.util.HashMap;
import java.util.Map;
import models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;
import util.PasswordValidator;

/**
 * AuthController - Kimlik Doğrulama REST Controller'ı
 * 
 * Bu controller, kullanıcı girişi (login) ve kayıt (register) işlemlerini REST API olarak yönetir.
 * Controller katmanının görevi: HTTP isteklerini almak, Service katmanını çağırmak
 * ve JSON response döndürmektir.
 * 
 * Mimari: REST Controller -> Service -> DAO
 * 
 * @author Esma
 * @version 2.0 (REST API)
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    // Service katmanını kullanıyoruz (DAO'ya direkt erişim yok!)
    private UserService userService;
    private UserDAO userDAO; // Kayıt işlemi için gerekli
    
    public AuthController() {
        this.userService = new UserService();
        this.userDAO = new UserDAO();
    }
    
    /**
     * Kullanıcı Girişi (Login)
     * REST Endpoint: POST /api/auth/login
     * 
     * @param requestBody JSON body: {"username": "...", "password": "..."}
     * @return JSON response: {"success": true/false, "user": {...}, "message": "..."}
     * @author Esma
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        
        // Validasyon işlemi - Controller katmanında yapılır
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Kullanıcı adı boş olamaz!");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (password == null || password.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Şifre boş olamaz!");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Service katmanını çağırarak giriş işlemini gerçekleştiriyoruz
        User user = userService.authenticate(username, password);
        
        if (user != null) {
            // Şifreyi response'da göstermiyoruz
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getUserId());
            userData.put("username", user.getUsername());
            userData.put("fullName", user.getFullName());
            userData.put("role", user.getRole());
            
            response.put("success", true);
            response.put("message", "Giriş başarılı! Hoş geldiniz, " + user.getFullName());
            response.put("user", userData);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Giriş başarısız! Kullanıcı adı veya şifre hatalı.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * Kullanıcı Kaydı (Register)
     * REST Endpoint: POST /api/auth/register
     * 
     * @param requestBody JSON body: {"username": "...", "password": "...", "fullName": "..."}
     * @return JSON response: {"success": true/false, "message": "..."}
     * @author Esma
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        String fullName = requestBody.get("fullName");
        
        // Validasyon işlemleri
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Kullanıcı adı boş olamaz!");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Şifre validasyonu - Klasik şifre gereksinimleri
        if (password == null || password.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Şifre boş olamaz!");
            return ResponseEntity.badRequest().body(response);
        }
        
        String passwordError = PasswordValidator.getValidationError(password);
        if (passwordError != null) {
            response.put("success", false);
            response.put("message", passwordError);
            return ResponseEntity.badRequest().body(response);
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Tam ad boş olamaz!");
            return ResponseEntity.badRequest().body(response);
        }
        
        // DAO'yu direkt çağırıyoruz çünkü UserService'de register metodu bulunmuyor
        // Not: İdeal mimaride bu işlem Service katmanında olmalıdır
        boolean success = userDAO.registerUser(username, password, fullName);
        
        if (success) {
            response.put("success", true);
            response.put("message", "Kayıt başarılı! Artık giriş yapabilirsiniz.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Kayıt başarısız! Kullanıcı adı zaten kullanılıyor olabilir.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
}

