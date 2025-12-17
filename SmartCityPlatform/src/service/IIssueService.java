package service;

import models.Issue;
import java.util.List;

public interface IIssueService {
    void createIssue(Issue issue); // Şikayet kaydı
    void prioritizeIssue(int issueId, String priority); // Öncelik atama
    List<Issue> getAllIssuesForAdmin(); // Tüm şikayetleri getirme
    List<Issue> getMyIssues(int userId); // Vatandaşın kendi şikayetleri
}