import java.sql.Connection;
import java.util.Scanner;

import controller.AdminController;
import controller.AuthController;
import controller.CitizenController;
import models.User;
import util.DBConnection;
import util.DatabaseSetup;

/**
 * Main - Ana Uygulama Sınıfı
 * 
 * Bu sınıf, Controller katmanını kullanarak kullanıcı arayüzünü yönetir.
 * Mimari: Main -> Controller -> Service -> DAO
 * 
 * Kişi 3 Esma (Controller Katmanı) tarafından güncellenmiştir.
 */
public class Main {
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
        
        // Veritabanı Bağlantı Testi
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("✅ Bağlantı Başarılı! 🚀 Veritabanına ulaşıldı.\n");
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("❌ Bağlantı Hatası! Lütfen MySQL'in açık olduğunu kontrol et.");
            System.out.println("💡 İpucu: DBConnection.java dosyasında şifrenizi kontrol edin.");
            e.printStackTrace();
            return;
        }

        // Controller'ları başlatıyoruz (Controller katmanı kullanıyoruz!)
        AuthController authController = new AuthController();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n========================================");
        System.out.println("  AKILLI ŞEHİR PLATFORMUNA HOŞ GELDİNİZ");
        System.out.println("========================================\n");
        
        // İlk menü: Giriş veya Kayıt
        System.out.println("1. Giriş Yap");
        System.out.println("2. Kayıt Ol");
        System.out.print("Seçiminiz: ");
        int initialChoice = Integer.parseInt(scanner.nextLine());

        User user = null;

        if (initialChoice == 1) {
            // Giriş işlemi
            System.out.print("Kullanıcı Adı: ");
            String username = scanner.nextLine();
            System.out.print("Şifre: ");
            String password = scanner.nextLine();
            
            // AuthController kullanarak giriş yapıyoruz
            user = authController.login(username, password);
        } else if (initialChoice == 2) {
            // Kayıt işlemi
            System.out.print("Kullanıcı Adı: ");
            String username = scanner.nextLine();
            System.out.print("Şifre: ");
            String password = scanner.nextLine();
            System.out.print("Tam Adınız: ");
            String fullName = scanner.nextLine();
            
            // AuthController kullanarak kayıt yapıyoruz
            boolean registered = authController.register(username, password, fullName);
            if (registered) {
                // Kayıt başarılıysa giriş yap
                user = authController.login(username, password);
            }
        }

        // Kullanıcı başarıyla giriş yaptıysa, rolüne göre menüyü göster
        if (user != null) {
            if (user.getRole().equalsIgnoreCase("ADMIN")) {
                showAdminMenu(scanner);
            } else {
                showCitizenMenu(user, scanner);
            }
        } else {
            System.out.println("❌ Giriş yapılamadı. Program sonlandırılıyor...");
        }
        
        scanner.close();
    }

    /**
     * Yönetici Menüsü - AdminController kullanarak işlemleri yönetir
     * 
     * @param scanner Kullanıcı girdisi için Scanner nesnesi
     * @author Esma
     */
    private static void showAdminMenu(Scanner scanner) {
        AdminController adminController = new AdminController();
        
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         YÖNETİCİ PANELİ");
            System.out.println("========================================");
            System.out.println("1. Tüm Şikayetleri Listele");
            System.out.println("2. Şikayet Durumu Güncelle");
            System.out.println("3. Şikayet Önceliklendir");
            System.out.println("4. Yeni Proje Oluştur");
            System.out.println("5. Proje Durumu Güncelle");
            System.out.println("6. Proje Başvurularını Görüntüle");
            System.out.println("7. Başvuru Onayla");
            System.out.println("8. Başvuru Reddet");
            System.out.println("9. Analitik Dashboard");
            System.out.println("0. Çıkış");
            System.out.print("Seçiminiz: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        // Controller kullanarak tüm şikayetleri listele
                        adminController.getAllIssues();
                        break;
                        
                    case 2:
                        System.out.print("Güncellenecek Şikayet ID: ");
                        int issueId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Yeni Durum (İnceleniyor/Çözüldü): ");
                        String status = scanner.nextLine();
                        adminController.updateIssueStatus(issueId, status);
                        break;
                        
                    case 3:
                        System.out.print("Önceliklendirilecek Şikayet ID: ");
                        int priorityIssueId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Öncelik (Yüksek/Orta/Düşük): ");
                        String priority = scanner.nextLine();
                        adminController.prioritizeIssue(priorityIssueId, priority);
                        break;
                        
                    case 4:
                        System.out.print("Proje Başlığı: ");
                        String title = scanner.nextLine();
                        System.out.print("Proje Açıklaması: ");
                        String desc = scanner.nextLine();
                        System.out.print("Başlangıç Tarihi (YYYY-MM-DD): ");
                        String start = scanner.nextLine();
                        System.out.print("Bitiş Tarihi (YYYY-MM-DD): ");
                        String end = scanner.nextLine();
                        adminController.createProject(title, desc, start, end);
                        break;
                        
                    case 5:
                        System.out.print("Proje ID: ");
                        int projectId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Yeni Durum (Açık/Kapalı/Tamamlandı): ");
                        String projectStatus = scanner.nextLine();
                        adminController.updateProjectStatus(projectId, projectStatus);
                        break;
                        
                    case 6:
                        System.out.print("Hangi Proje ID'nin başvuruları?: ");
                        int reviewProjectId = Integer.parseInt(scanner.nextLine());
                        adminController.reviewApplications(reviewProjectId);
                        break;
                        
                    case 7:
                        System.out.print("Onaylanacak Başvuru ID: ");
                        int approveAppId = Integer.parseInt(scanner.nextLine());
                        adminController.approveApplication(approveAppId);
                        break;
                        
                    case 8:
                        System.out.print("Reddedilecek Başvuru ID: ");
                        int rejectAppId = Integer.parseInt(scanner.nextLine());
                        adminController.rejectApplication(rejectAppId);
                        break;
                        
                    case 9:
                        adminController.viewAnalyticsDashboard();
                        break;
                        
                    case 0:
                        System.out.println("👋 Güvenli çıkış yapılıyor...");
                        return;
                        
                    default:
                        System.out.println("❌ Geçersiz seçim! Lütfen 0-9 arası bir sayı girin.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Hata: Lütfen geçerli bir sayı girin!");
            } catch (Exception e) {
                System.out.println("❌ Bir hata oluştu: " + e.getMessage());
            }
        }
    }

    /**
     * Vatandaş Menüsü - CitizenController kullanarak işlemleri yönetir
     * 
     * @param user Giriş yapan vatandaş kullanıcısı
     * @param scanner Kullanıcı girdisi için Scanner nesnesi
     * @author Esma
     */
    private static void showCitizenMenu(User user, Scanner scanner) {
        CitizenController citizenController = new CitizenController();
        
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         VATANDAŞ MENÜSÜ");
            System.out.println("========================================");
            System.out.println("Hoş geldiniz, " + user.getFullName() + "!");
            System.out.println("\n1. Yeni Şikayet/Talep Oluştur");
            System.out.println("2. Şikayetlerimi Görüntüle");
            System.out.println("3. Açık Projeleri Görüntüle");
            System.out.println("4. Projeye Başvur");
            System.out.println("5. Başvurularımı Görüntüle");
            System.out.println("0. Çıkış");
            System.out.print("Seçiminiz: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        // Controller kullanarak şikayet oluştur
                        System.out.print("Şikayet Başlığı: ");
                        String title = scanner.nextLine();
                        System.out.print("Açıklama: ");
                        String desc = scanner.nextLine();
                        citizenController.createIssue(user.getUserId(), title, desc);
                        break;
                        
                    case 2:
                        // Controller kullanarak kendi şikayetlerini görüntüle
                        citizenController.getMyIssues(user.getUserId());
                        break;
                        
                    case 3:
                        // Controller kullanarak açık projeleri görüntüle
                        citizenController.viewOpenProjects();
                        break;
                        
                    case 4:
                        // Controller kullanarak projeye başvur
                        System.out.print("Başvurulacak Proje ID: ");
                        int projectId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Başvuru Notunuz (opsiyonel): ");
                        String note = scanner.nextLine();
                        citizenController.applyForProject(projectId, user.getUserId(), note);
                        break;
                        
                    case 5:
                        // Controller kullanarak başvuruları görüntüle
                        citizenController.viewMyApplications(user.getUserId());
                        break;
                        
                    case 0:
                        System.out.println("👋 Güvenli çıkış yapılıyor...");
                        return;
                        
                    default:
                        System.out.println("❌ Geçersiz seçim! Lütfen 0-5 arası bir sayı girin.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Hata: Lütfen geçerli bir sayı girin!");
            } catch (Exception e) {
                System.out.println("❌ Bir hata oluştu: " + e.getMessage());
            }
        }
    }
}