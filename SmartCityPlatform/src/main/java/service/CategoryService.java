package service;

import dao.CategoryDAO;

/**
 * CategoryService - Kategori İş Mantığı Servisi
 * 
 * Bu servis, kategori ile ilgili iş mantığını yönetir.
 * 
 * @author Elif
 */
public class CategoryService {
    
    private CategoryDAO categoryDAO = new CategoryDAO();
    
    /**
     * En çok şikayet alan kategorileri getirir (Stored Procedure kullanarak)
     * 
     * @param limit Kaç kategori gösterilecek (örn: 5)
     * @return List of top category maps
     * @author Elif
     */
    public java.util.List<java.util.Map<String, Object>> getTopCategories(int limit) {
        return categoryDAO.getTopCategories(limit);
    }
    
    /**
     * Eski metod uyumluluk için (deprecated)
     * @deprecated Use getTopCategories() instead
     */
    @Deprecated
    public void printTopCategories(int limit) {
        categoryDAO.printTopCategories(limit);
    }
}

