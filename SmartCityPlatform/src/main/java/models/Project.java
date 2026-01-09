package models;

import java.util.Date;

public class Project {
    private int projectId;
    private String title;
    private String description;
    private String status;
    private Date startDate;
    private Date endDate;

    public Project() {} // Boş constructor (Gereklidir)

    public Project(int projectId, String title, String description, String status, Date startDate, Date endDate) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter ve Setter metotları
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
}