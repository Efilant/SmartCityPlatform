package models;

public class Category {
    private int categoryId;
    private String name;
    private String responsibleUnit;

    public Category() {}

    public Category(int categoryId, String name, String responsibleUnit) {
        this.categoryId = categoryId;
        this.name = name;
        this.responsibleUnit = responsibleUnit;
    }

    // Getters and Setters
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getResponsibleUnit() { return responsibleUnit; }
    public void setResponsibleUnit(String responsibleUnit) { this.responsibleUnit = responsibleUnit; }
}