package util;

import java.util.regex.Pattern;

/**
 * PasswordValidator - Şifre validasyonu için utility sınıfı
 * 
 * Klasik şifre gereksinimleri:
 * - Minimum 8 karakter
 * - En az bir büyük harf (A-Z)
 * - En az bir küçük harf (a-z)
 * - En az bir rakam (0-9)
 * - En az bir özel karakter (!@#$%^&*()_+-=[]{}|;:,.<>?)
 */
public class PasswordValidator {
    
    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?]");
    
    /**
     * Şifrenin geçerli olup olmadığını kontrol eder
     * 
     * @param password Kontrol edilecek şifre
     * @return Şifre geçerliyse true, değilse false
     */
    public static boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // Minimum uzunluk kontrolü
        if (password.length() < MIN_LENGTH) {
            return false;
        }
        
        // Büyük harf kontrolü
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return false;
        }
        
        // Küçük harf kontrolü
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return false;
        }
        
        // Rakam kontrolü
        if (!DIGIT_PATTERN.matcher(password).find()) {
            return false;
        }
        
        // Özel karakter kontrolü
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Şifre validasyon hatalarını detaylı olarak döndürür
     * 
     * @param password Kontrol edilecek şifre
     * @return Hata mesajı (geçerliyse null)
     */
    public static String getValidationError(String password) {
        if (password == null || password.isEmpty()) {
            return "Şifre boş olamaz!";
        }
        
        if (password.length() < MIN_LENGTH) {
            return "Şifre en az " + MIN_LENGTH + " karakter olmalıdır!";
        }
        
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return "Şifre en az bir büyük harf (A-Z) içermelidir!";
        }
        
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return "Şifre en az bir küçük harf (a-z) içermelidir!";
        }
        
        if (!DIGIT_PATTERN.matcher(password).find()) {
            return "Şifre en az bir rakam (0-9) içermelidir!";
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return "Şifre en az bir özel karakter (!@#$%^&*()_+-=[]{}|;:,.<>?) içermelidir!";
        }
        
        return null; // Şifre geçerli
    }
    
    /**
     * Şifre gereksinimlerini açıklayan mesaj döndürür
     * 
     * @return Şifre gereksinimleri açıklaması
     */
    public static String getRequirements() {
        return "Şifre gereksinimleri:\n" +
               "• En az " + MIN_LENGTH + " karakter\n" +
               "• En az bir büyük harf (A-Z)\n" +
               "• En az bir küçük harf (a-z)\n" +
               "• En az bir rakam (0-9)\n" +
               "• En az bir özel karakter (!@#$%^&*()_+-=[]{}|;:,.<>?)";
    }
}

