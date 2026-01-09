package controller;

import dao.CategoryDAO;
import models.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CategoryController - Kategori REST Controller'ı
 * 
 * Bu controller, kategori ile ilgili işlemleri REST API olarak yönetir.
 * 
 * @author Smart City Platform Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    private CategoryDAO categoryDAO;
    
    public CategoryController() {
        this.categoryDAO = new CategoryDAO();
    }
    
    /**
     * Tüm kategorileri listeleme
     * REST Endpoint: GET /api/categories
     * 
     * @return JSON response: {"success": true, "categories": [...]}
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Category> categories = categoryDAO.findAll();
            
            response.put("success", true);
            response.put("categories", categories);
            response.put("count", categories.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Kategoriler yüklenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * ID'ye göre kategori getirme
     * REST Endpoint: GET /api/categories/{id}
     * 
     * @param id Kategori ID'si
     * @return JSON response: {"success": true, "category": {...}}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Category category = categoryDAO.findById(id);
            
            if (category != null) {
                response.put("success", true);
                response.put("category", category);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Kategori bulunamadı!");
                return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Kategori yüklenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

