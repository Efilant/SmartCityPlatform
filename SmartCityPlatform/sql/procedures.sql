USE akilli_sehir_db;

DELIMITER //
-- Admin için genel bir istatistik özeti getiren prosedür
CREATE PROCEDURE GetSystemStats()
BEGIN
    SELECT 
        (SELECT COUNT(*) FROM Issues) as total_issues,
        (SELECT COUNT(*) FROM Projects WHERE status = 'Açık') as active_projects,
        (SELECT COUNT(*) FROM Applications) as total_applications;
END //
DELIMITER ;

-- Admin'in hangi kategoride ne kadar başarılı olduğunu görmesini sağlar.
DELIMITER //
CREATE PROCEDURE GetCategorySuccessRate()
BEGIN
    SELECT 
        c.name AS Kategori,
        COUNT(i.issue_id) AS Toplam_Sikayet,
        SUM(CASE WHEN i.status = 'Çözüldü' THEN 1 ELSE 0 END) AS Cozulen_Sikayet,
        ROUND((SUM(CASE WHEN i.status = 'Çözüldü' THEN 1 ELSE 0 END) / COUNT(i.issue_id)) * 100, 2) AS Basari_Yuzdesi
    FROM Categories c
    LEFT JOIN Issues i ON c.category_id = i.category_id
    GROUP BY c.category_id;
END //
DELIMITER ;

-- Belirli bir vatandaşın sistemdeki tüm izlerini (kaç şikayet, kaç başvuru) tek seferde getirir.
DELIMITER //
CREATE PROCEDURE GetUserActivitySummary(IN p_user_id INT)
BEGIN
    SELECT 
        (SELECT COUNT(*) FROM Issues WHERE user_id = p_user_id) as sikayet_sayisi,
        (SELECT COUNT(*) FROM Applications WHERE user_id = p_user_id) as basvuru_sayisi;
END //
DELIMITER ;

-- Belirli bir kategorideki bekleyen şikayetleri getirir
DELIMITER //
CREATE PROCEDURE GetPendingIssuesByCategory(IN p_category_id INT)
BEGIN
    SELECT 
        i.issue_id,
        i.title,
        i.description,
        i.status,
        i.created_at,
        u.full_name AS vatandas_adi
    FROM Issues i
    INNER JOIN Users u ON i.user_id = u.user_id
    WHERE i.category_id = p_category_id 
    AND i.status IN ('Yeni', 'İnceleniyor')
    ORDER BY i.created_at DESC;
END //
DELIMITER ;

-- Belirli bir projeye yapılan başvuruların detaylı listesini getirir
DELIMITER //
CREATE PROCEDURE GetProjectApplications(IN p_project_id INT)
BEGIN
    SELECT 
        a.application_id,
        a.application_date,
        a.status,
        a.notes,
        u.full_name AS basvuran_adi,
        u.username AS basvuran_kullanici_adi,
        p.title AS proje_basligi
    FROM Applications a
    INNER JOIN Users u ON a.user_id = u.user_id
    INNER JOIN Projects p ON a.project_id = p.project_id
    WHERE a.project_id = p_project_id
    ORDER BY a.application_date DESC;
END //
DELIMITER ;

-- Kullanıcının duruma göre şikayetlerini getirir
DELIMITER //
CREATE PROCEDURE GetUserIssuesByStatus(IN p_user_id INT, IN p_status VARCHAR(20))
BEGIN
    SELECT 
        i.issue_id,
        i.title,
        i.description,
        i.status,
        i.created_at,
        c.name AS kategori_adi
    FROM Issues i
    LEFT JOIN Categories c ON i.category_id = c.category_id
    WHERE i.user_id = p_user_id 
    AND i.status = p_status
    ORDER BY i.created_at DESC;
END //
DELIMITER ;

-- Son 30 günün istatistiklerini getirir
DELIMITER //
CREATE PROCEDURE GetMonthlyStats()
BEGIN
    SELECT 
        DATE(created_at) AS tarih,
        COUNT(*) AS gunluk_sikayet_sayisi,
        SUM(CASE WHEN status = 'Çözüldü' THEN 1 ELSE 0 END) AS cozulen_sayisi
    FROM Issues
    WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
    GROUP BY DATE(created_at)
    ORDER BY tarih DESC;
END //
DELIMITER ;

-- En çok şikayet alan kategorileri getirir (Top 5)
DELIMITER //
CREATE PROCEDURE GetTopCategories(IN p_limit INT)
BEGIN
    SELECT 
        c.name AS kategori_adi,
        COUNT(i.issue_id) AS toplam_sikayet,
        SUM(CASE WHEN i.status = 'Çözüldü' THEN 1 ELSE 0 END) AS cozulen_sikayet,
        c.responsible_unit AS sorumlu_birim
    FROM Categories c
    LEFT JOIN Issues i ON c.category_id = i.category_id
    GROUP BY c.category_id, c.name, c.responsible_unit
    ORDER BY toplam_sikayet DESC
    LIMIT p_limit;
END //
DELIMITER ;
