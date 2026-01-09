package models;

import java.sql.Timestamp;

public class Application {
    private int applicationId;
    private int projectId;
    private int userId;
    private Timestamp applicationDate;
    private String status; // 'Beklemede', 'Onaylandı', 'Reddedildi'
    private String notes;

    // Boş Constructor (Veritabanından veri çekerken şarttır)
    public Application() {}

    // Tüm Parametreli Constructor
    public Application(int applicationId, int projectId, int userId, Timestamp applicationDate, String status, String notes) {
        this.applicationId = applicationId;
        this.projectId = projectId;
        this.userId = userId;
        this.applicationDate = applicationDate;
        this.status = status;
        this.notes = notes;
    }

    // Getter ve Setter Metotları
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getApplicationDate() { return applicationDate; }
    public void setApplicationDate(Timestamp applicationDate) { this.applicationDate = applicationDate; }
}