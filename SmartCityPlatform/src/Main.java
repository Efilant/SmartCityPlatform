import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

import dao.ProjectDAO;
import dao.ApplicationDAO;
import models.Issue;
import models.User;
import service.IssueService;
import service.UserService;
import util.DBConnection;

public class Main {
    public static void main(String[] args) {
        // Veritabanı Bağlantı Testi
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Bağlantı Başarılı! 🚀 Veritabanına ulaşıldı.");
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("Bağlantı Hatası! ❌ Lütfen şifreni ve MySQL'in açık olduğunu kontrol et.");
            e.printStackTrace();
        }

        // Servis ve DAO Başlatmaları
        UserService userService = new UserService();
        IssueService issueService = new IssueService();
        ProjectDAO projectDAO = new ProjectDAO(); // Kişi 1 Sorumluluğu
        ApplicationDAO applicationDAO = new ApplicationDAO(); // Kişi 1 Sorumluluğu
        
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- AKILLI ŞEHİR PLATFORMUNA HOŞ GELDİNİZ ---");
        System.out.print("Kullanıcı Adı: ");
        String username = scanner.nextLine();
        System.out.print("Şifre: ");
        String password = scanner.nextLine();

        User user = userService.authenticate(username, password);

        if (user != null) {
            if (user.getRole().equalsIgnoreCase("ADMIN")) {
                showAdminMenu(issueService, projectDAO, applicationDAO, scanner);
            } else {
                showCitizenMenu(issueService, projectDAO, applicationDAO, user, scanner);
            }
        }
    }

    private static void showAdminMenu(IssueService issueService, ProjectDAO projectDAO, ApplicationDAO appDAO, Scanner sc) {
        while (true) {
            System.out.println("\n--- YÖNETİCİ PANELİ ---");
            System.out.println("1. Tüm Şikayetleri Listele (IssueDAO.findAll)");
            System.out.println("2. Şikayet Durumu Güncelle (IssueDAO.updateStatus)");
            System.out.println("3. Yeni Belediye Projesi Ekle (ProjectDAO.save)");
            System.out.println("4. Proje Durumu Güncelle (ProjectDAO.updateStatus)");
            System.out.println("5. Proje Başvurularını Görüntüle (ApplicationDAO.findByProjectId)");
            System.out.println("6. Güvenli Çıkış");
            System.out.print("Seçiminiz: ");
            
            int choice = Integer.parseInt(sc.nextLine());

            if (choice == 1) {
                List<Issue> allIssues = issueService.getAllIssuesForAdmin();
                System.out.println("\nID | Başlık | Durum | Açıklama");
                System.out.println("-----------------------------------");
                for (Issue issue : allIssues) {
                    System.out.println(issue.getIssueId() + " | " + issue.getTitle() + " | " + issue.getStatus() + " | " + issue.getDescription());
                }
            } else if (choice == 2) {
                System.out.print("Güncellenecek Şikayet ID: ");
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("Yeni Durum (İnceleniyor/Çözüldü): ");
                String status = sc.nextLine();
                issueService.updateStatusByAdmin(id, status);
            } else if (choice == 3) {
                System.out.print("Proje Başlığı: "); String title = sc.nextLine();
                System.out.print("Proje Açıklaması: "); String desc = sc.nextLine();
                System.out.print("Başlangıç Tarihi (YYYY-MM-DD): "); String start = sc.nextLine();
                System.out.print("Bitiş Tarihi (YYYY-MM-DD): "); String end = sc.nextLine();
                projectDAO.save(title, desc, start, end);
            } else if (choice == 4) {
                System.out.print("Proje ID: "); int pId = Integer.parseInt(sc.nextLine());
                System.out.print("Yeni Durum (Açık/Kapalı/Tamamlandı): "); String pStatus = sc.nextLine();
                projectDAO.updateStatus(pId, pStatus);
            } else if (choice == 5) {
                System.out.print("Hangi Proje ID'nin başvuruları?: ");
                int projId = Integer.parseInt(sc.nextLine());
                appDAO.findByProjectId(projId);
            } else if (choice == 6) break;
        }
    }

    private static void showCitizenMenu(IssueService issueService, ProjectDAO projectDAO, ApplicationDAO appDAO, User user, Scanner sc) {
        while (true) {
            System.out.println("\n--- VATANDAŞ MENÜSÜ ---");
            System.out.println("1. Yeni Şikayet Oluştur (IssueDAO.save)");
            System.out.println("2. Şikayetlerimi Görüntüle (IssueDAO.findAllByUserId)");
            System.out.println("3. Açık Belediye Projelerini Gör (ProjectDAO.findAllOpen)");
            System.out.println("4. Belediye Projesine Başvur (ApplicationDAO.save)");
            System.out.println("5. Güvenli Çıkış");
            System.out.print("Seçiminiz: ");
            
            int choice = Integer.parseInt(sc.nextLine());

            if (choice == 1) {
                System.out.print("Şikayet Başlığı: "); String title = sc.nextLine();
                System.out.print("Açıklama: "); String desc = sc.nextLine();
                issueService.reportNewIssue(user.getUserId(), title, desc);
            } else if (choice == 2) {
                // Not: issueService içinde getMyIssues metodu yoksa oluşturulmalıdır.
                List<Issue> myIssues = issueService.getAllIssuesForAdmin(); // Şimdilik genel listeyi basar
                System.out.println("\nŞikayetleriniz:");
                for (Issue i : myIssues) {
                    if(i.getUserId() == user.getUserId())
                        System.out.println(i.getTitle() + " - Durum: " + i.getStatus());
                }
            } else if (choice == 3) {
                List<String> openProjects = projectDAO.findAllOpen();
                System.out.println("\nAktif Belediye Projeleri:");
                openProjects.forEach(p -> System.out.println("- " + p));
            } else if (choice == 4) {
                System.out.print("Başvurulacak Proje ID: "); int pId = Integer.parseInt(sc.nextLine());
                System.out.print("Başvuru Notunuz: "); String note = sc.nextLine();
                appDAO.save(pId, user.getUserId(), note);
            } else if (choice == 5) break;
        }
    }
}