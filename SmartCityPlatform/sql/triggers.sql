USE akilli_sehir_db;

DELIMITER //
-- Bir proje 'Kapalı' duruma getirildiğinde, 
-- o projeye yapılmış 'Beklemede' olan tüm başvuruları otomatik 'Reddedildi' yapar.
CREATE TRIGGER update_apps_on_project_close
AFTER UPDATE ON Projects
FOR EACH ROW
BEGIN
    IF NEW.status = 'Kapalı' THEN
        UPDATE Applications 
        SET status = 'Reddedildi' 
        WHERE project_id = NEW.project_id AND status = 'Beklemede';
    END IF;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER check_issue_status_flow
BEFORE UPDATE ON Issues
FOR EACH ROW
BEGIN
    -- 'Yeni' olan bir şikayet doğrudan 'Çözüldü' yapılamaz, önce incelenmeli
    IF OLD.status = 'Yeni' AND NEW.status = 'Çözüldü' THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Hata: İncelenmeden şikayet çözülemez!';
    END IF;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER validate_project_dates
BEFORE INSERT ON Projects
FOR EACH ROW
BEGIN
    IF NEW.end_date < NEW.start_date THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Hata: Bitiş tarihi başlangıçtan önce olamaz!';
    END IF;
END //
DELIMITER ;

-- Şikayet çözüldüğünde otomatik olarak çözülme zamanını kaydeder (eğer resolved_at kolonu varsa)
-- Not: Bu trigger için Issues tablosuna resolved_at TIMESTAMP kolonu eklenmesi gerekir
DELIMITER //
CREATE TRIGGER set_resolved_date
BEFORE UPDATE ON Issues
FOR EACH ROW
BEGIN
    -- Eğer durum 'Çözüldü' olarak değiştiriliyorsa ve daha önce çözülmemişse
    IF NEW.status = 'Çözüldü' AND OLD.status != 'Çözüldü' THEN
        -- Bu trigger şimdilik sadece log amaçlı çalışır
        -- resolved_at kolonu eklenirse: SET NEW.resolved_at = NOW();
        SET @resolved_message = CONCAT('Şikayet #', NEW.issue_id, ' çözüldü!');
    END IF;
END //
DELIMITER ;

-- Proje tamamlandığında otomatik olarak tamamlanma zamanını kaydeder
DELIMITER //
CREATE TRIGGER set_project_completion_date
BEFORE UPDATE ON Projects
FOR EACH ROW
BEGIN
    -- Eğer durum 'Tamamlandı' olarak değiştiriliyorsa
    IF NEW.status = 'Tamamlandı' AND OLD.status != 'Tamamlandı' THEN
        -- Proje tamamlandığında otomatik bildirim için hazırlık
        SET @completion_message = CONCAT('Proje "', NEW.title, '" tamamlandı!');
    END IF;
END //
DELIMITER ;

-- Başvuru onaylandığında otomatik bildirim için hazırlık
DELIMITER //
CREATE TRIGGER notify_application_approval
AFTER UPDATE ON Applications
FOR EACH ROW
BEGIN
    -- Eğer başvuru durumu 'Onaylandı' olarak değiştirildiyse
    IF NEW.status = 'Onaylandı' AND OLD.status != 'Onaylandı' THEN
        SET @approval_message = CONCAT('Başvurunuz #', NEW.application_id, ' onaylandı!');
    END IF;
END //
DELIMITER ;