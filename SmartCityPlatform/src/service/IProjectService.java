package service;

import models.Project;
import java.util.List;

public interface IProjectService {
    void createProject(Project project); // Proje oluşturma
    List<Project> getOpenProjects(); // Açık projeleri listeleme
    void applyForProject(int projectId, int userId); // Projeye başvuru
    void approveApplication(int applicationId); // Başvuruyu onaylama
    void rejectApplication(int applicationId); // Başvuruyu reddetme
}