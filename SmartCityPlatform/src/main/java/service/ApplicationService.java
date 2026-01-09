package service;

import dao.ApplicationDAO;

/**
 * ApplicationService - Başvuru İş Mantığı Servisi
 * 
 * Bu servis, başvuru ile ilgili iş mantığını yönetir.
 * 
 * @author Elif
 */
public class ApplicationService {
    
    private ApplicationDAO applicationDAO = new ApplicationDAO();
    
    /**
     * Projeye yapılan başvuruların detaylı listesini getirir (Stored Procedure kullanarak)
     * 
     * @param projectId Proje ID'si
     * @return List of application maps with details
     * @author Elif
     */
    public java.util.List<java.util.Map<String, Object>> getProjectApplications(int projectId) {
        return applicationDAO.getProjectApplications(projectId);
    }
    
    /**
     * Eski metod uyumluluk için (deprecated)
     * @deprecated Use getProjectApplications() instead
     */
    @Deprecated
    public void printProjectApplications(int projectId) {
        applicationDAO.printProjectApplications(projectId);
    }
}

