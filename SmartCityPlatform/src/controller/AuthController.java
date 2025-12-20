package controller;

import models.User;
import service.UserService;
import dao.UserDAO;

/**
 * AuthController - Kimlik Doğrulama Controller'ı
 * 
 * Bu controller, kullanıcı girişi (login) ve kayıt (register) işlemlerini yönetir.
 * Controller katmanının görevi: Kullanıcıdan gelen istekleri almak, Service katmanını çağırmak
 * ve sonucu döndürmektir.
 * 
 * Mimari: Controller -> Service -> DAO
 * 
 * @author Esma
 * @version 1.0
 */
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
     * Endpoint: /login
     * 
     * @param username Kullanıcı adı
     * @param password Şifre
     * @return Giriş başarılıysa User nesnesi, değilse null
     * @author Esma
     */
    public User login(String username, String password) {
        // Validasyon işlemi - Controller katmanında yapılır
        if (username == null || username.trim().isEmpty()) {
            System.out.println("❌ Hata: Kullanıcı adı boş olamaz!");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ Hata: Şifre boş olamaz!");
            return null;
        }
        
        // Service katmanını çağırarak giriş işlemini gerçekleştiriyoruz
        User user = userService.authenticate(username, password);
        
        if (user != null) {
            System.out.println("✅ Giriş başarılı! Hoş geldiniz, " + user.getFullName());
        } else {
            System.out.println("❌ Giriş başarısız! Kullanıcı adı veya şifre hatalı.");
        }
        
        return user;
    }
    
    /**
     * Kullanıcı Kaydı (Register)
     * Endpoint: /register
     * 
     * @param username Kullanıcı adı
     * @param password Şifre
     * @param fullName Tam ad
     * @return Kayıt başarılıysa true, değilse false
     * @author Esma
     */
    public boolean register(String username, String password, String fullName) {
        // Validasyon işlemleri
        if (username == null || username.trim().isEmpty()) {
            System.out.println("❌ Hata: Kullanıcı adı boş olamaz!");
            return false;
        }
        
        if (password == null || password.trim().isEmpty() || password.length() < 4) {
            System.out.println("❌ Hata: Şifre en az 4 karakter olmalıdır!");
            return false;
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            System.out.println("❌ Hata: Tam ad boş olamaz!");
            return false;
        }
        
        // DAO'yu direkt çağırıyoruz çünkü UserService'de register metodu bulunmuyor
        // Not: İdeal mimaride bu işlem Service katmanında olmalıdır
        boolean success = userDAO.registerUser(username, password, fullName);
        
        if (success) {
            System.out.println("✅ Kayıt başarılı! Artık giriş yapabilirsiniz.");
        } else {
            System.out.println("❌ Kayıt başarısız! Kullanıcı adı zaten kullanılıyor olabilir.");
        }
        
        return success;
    }
}

