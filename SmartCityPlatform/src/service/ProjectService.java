package service;

import dao.ProjectDAO;
import dao.ApplicationDAO;
import models.Project;
import java.util.List;
import java.util.ArrayList;
import java.sql.Date;

/**
 * ProjectService - Proje İş Mantığı Servisi
 * 
 * Bu servis, proje ile ilgili iş mantığını yönetir.
 * IProjectService interface'ini implement eder.
 * 
 * @author Smart City Platform Team
 * @version 1.0
 */
public class ProjectService implements IProjectService {
    
    private ProjectDAO projectDAO;
    private ApplicationDAO applicationDAO;
    
    public ProjectService() {
        this.projectDAO = new ProjectDAO();
        this.applicationDAO = new ApplicationDAO();
    }
    
    /**
     * Yeni proje oluşturma
     * 
     * @param project Proje nesnesi
     */
    @Override
    public void createProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Proje nesnesi boş olamaz!");
        }
        
        if (project.getTitle() == null || project.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Proje başlığı boş olamaz!");
        }
        
        if (project.getDescription() == null || project.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Proje açıklaması boş olamaz!");
        }
        
        // ProjectDAO'da save metodu String parametreler alıyor, bu yüzden dönüştürüyoruz
        String startDate = project.getStartDate() != null ? 
            project.getStartDate().toString() : new Date(System.currentTimeMillis()).toString();
        String endDate = project.getEndDate() != null ? 
            project.getEndDate().toString() : new Date(System.currentTimeMillis() + 86400000L * 30).toString(); // 30 gün sonra
        
        projectDAO.save(project.getTitle(), project.getDescription(), startDate, endDate);
    }
    
    /**
     * Açık projeleri listeleme
     * 
     * @return Açık projeler listesi
     */
    @Override
    public List<Project> getOpenProjects() {
        // ProjectDAO'da findAllOpen() metodu String listesi döndürüyor
        // Bu metodun Project nesneleri döndürmesi için DAO'yu güncellemek gerekir
        // Şimdilik boş liste döndürüyoruz veya DAO'yu kullanarak manuel oluşturuyoruz
        List<String> projectTitles = projectDAO.findAllOpen();
        List<Project> projects = new ArrayList<>();
        
        // Not: ProjectDAO'da ID ve diğer bilgileri almak için yeni bir metod eklenmeli
        // Şimdilik sadece başlıkları kullanıyoruz
        for (int i = 0; i < projectTitles.size(); i++) {
            Project project = new Project();
            project.setTitle(projectTitles.get(i));
            project.setStatus("Açık");
            projects.add(project);
        }
        
        return projects;
    }
    
    /**
     * Projeye başvuru yapma
     * 
     * @param projectId Proje ID'si
     * @param userId Kullanıcı ID'si
     */
    @Override
    public void applyForProject(int projectId, int userId) {
        if (projectId <= 0) {
            throw new IllegalArgumentException("Geçersiz proje ID!");
        }
        
        if (userId <= 0) {
            throw new IllegalArgumentException("Geçersiz kullanıcı ID!");
        }
        
        applicationDAO.save(projectId, userId, "");
    }
    
    /**
     * Başvuruyu onaylama
     * 
     * @param applicationId Başvuru ID'si
     */
    @Override
    public void approveApplication(int applicationId) {
        if (applicationId <= 0) {
            throw new IllegalArgumentException("Geçersiz başvuru ID!");
        }
        
        applicationDAO.updateStatus(applicationId, "Onaylandı");
    }
    
    /**
     * Başvuruyu reddetme
     * 
     * @param applicationId Başvuru ID'si
     */
    @Override
    public void rejectApplication(int applicationId) {
        if (applicationId <= 0) {
            throw new IllegalArgumentException("Geçersiz başvuru ID!");
        }
        
        applicationDAO.updateStatus(applicationId, "Reddedildi");
    }
}

