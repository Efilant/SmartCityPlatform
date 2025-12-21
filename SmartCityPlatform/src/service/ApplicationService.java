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
     * Projeye yapılan başvuruların detaylı listesini gösterir (Stored Procedure kullanarak)
     * 
     * @param projectId Proje ID'si
     * @author Elif
     */
    public void printProjectApplications(int projectId) {
        applicationDAO.printProjectApplications(projectId);
    }
}

