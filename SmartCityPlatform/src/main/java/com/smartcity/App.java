package com.smartcity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import util.DatabaseSetup;

/**
 * Smart City Platform - REST API Ana Uygulama Sınıfı
 * 
 * Bu sınıf Spring Boot uygulamasını başlatır ve REST API sunucusunu çalıştırır.
 * 
 * @author Smart City Platform Team
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"controller", "service", "dao", "models", "util", "com.smartcity"})
public class App {
    public static void main(String[] args) {
        System.out.println("\n========================================");
        System.out.println("  VERİTABANI KURULUMU KONTROL EDİLİYOR");
        System.out.println("========================================\n");
        
        // Veritabanını otomatik kur
        if (!DatabaseSetup.setupDatabase()) {
            System.out.println("❌ Veritabanı kurulumu başarısız! MySQL'in çalıştığından emin olun.");
            System.out.println("💡 İpucu: XAMPP/WAMP kullanıyorsanız, MySQL servisinin başlatıldığından emin olun.");
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println("  REST API SUNUCUSU BAŞLATILIYOR");
        System.out.println("========================================\n");
        
        // Spring Boot uygulamasını başlat
        SpringApplication.run(App.class, args);
        
        System.out.println("\n✅ REST API sunucusu başarıyla başlatıldı!");
        System.out.println("🌐 API Endpoint'leri: http://localhost:8080/api");
        System.out.println("\n📚 Kullanılabilir Endpoint'ler:");
        System.out.println("   - POST   /api/auth/login");
        System.out.println("   - POST   /api/auth/register");
        System.out.println("   - GET    /api/issues");
        System.out.println("   - POST   /api/issues");
        System.out.println("   - GET    /api/projects");
        System.out.println("   - POST   /api/projects");
        System.out.println("   - POST   /api/applications");
        System.out.println("   - GET    /api/applications");
        System.out.println("\n💡 API dokümantasyonu için Swagger UI: http://localhost:8080/swagger-ui.html");
    }
}
