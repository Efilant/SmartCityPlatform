package models;

public class Issue {
    private int issueId;
    private int userId;
    private String title;
    private String description;
    private String status;
    public Issue() {
}
    
    public Issue(int issueId, int userId, String title, String description, String status) {
        this.issueId = issueId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = status;
    }
    public int getIssueId() {
        return issueId;
    }
    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}