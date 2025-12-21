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
     * En çok şikayet alan kategorileri gösterir (Stored Procedure kullanarak)
     * 
     * @param limit Kaç kategori gösterilecek (örn: 5)
     * @author Elif
     */
    public void printTopCategories(int limit) {
        categoryDAO.printTopCategories(limit);
    }
}

