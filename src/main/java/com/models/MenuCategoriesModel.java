package com.models;
public class MenuCategoriesModel {

    private int category_id;
    private int restaurant_id;
    private String category_name;
    private String category_description;
    private Boolean is_active;

    // Constructors
    public MenuCategoriesModel() {
    }

    public MenuCategoriesModel(int id, int R_id, String Name, String Description, Boolean active) {
        this.category_id = id;
        this.restaurant_id = R_id;
        this.category_name = Name;
        this.category_description = Description;
        this.is_active = active;
    }

    public MenuCategoriesModel(MenuCategoriesModel other) {
        this.category_id = other.category_id;
        this.restaurant_id = other.restaurant_id;
        this.category_name = other.category_name;
        this.category_description = other.category_description;
        this.is_active = other.is_active;
    }

    // Getters and setters
    public int getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int id) {
        this.category_id = id;
    }

    public int getRestaurantId() {
        return restaurant_id;
    }

    public void getRestaurantId(int id) {
        this.restaurant_id = id;
    }

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String Name) {
        this.category_name = Name;
    }

    public String getCategoryDesc() {
        return category_description;
    }

    public void setCategoryDesc(String Desc) {
        this.category_description = Desc;
    }

    public Boolean getCategoryActive() {
        return is_active;
    }

    public void setCategoryActive(Boolean Active) {
        this.is_active = Active;
    }
}
