package com.models;
import java.sql.Timestamp;
public class MenuItemsModel {

    private int item_id;
    private int restaurant_id;
    private int category_id;
    private String item_name;
    private int item_quantity;
    private String item_description;
    private float item_price;
    private float item_discounted_price;
    private int item_preparation_time;
    private Boolean is_available;
    private String item_image_url;
    private int item_display_order;
    private Timestamp item_created_at;
    private Timestamp item_updated_at;

    // Constructors
    public MenuItemsModel() {
    }

    public MenuItemsModel(int id, int R_id, int C_id, String Name, int Quantity, String Description, float Price, float item_discounted_price,
        int Preparation_Time, Boolean active, String Image_Url, int Display_Order, Timestamp Created_at, Timestamp Updated_at) {
        this.item_id = id;
        this.restaurant_id = R_id;
        this.category_id = C_id;
        this.item_name = Name;
        this.item_quantity = Quantity;
        this.item_description = Description;
        this.item_price = Price;
        this.item_discounted_price = item_discounted_price;
        this.item_preparation_time = Preparation_Time;
        this.is_available = active;
        this.item_image_url = Image_Url;
        this.item_display_order = Display_Order;
        this.item_created_at = Created_at;
        this.item_updated_at = Updated_at;
    }

    public MenuItemsModel(MenuItemsModel other) {
        this.item_id = other.item_id;
        this.restaurant_id = other.restaurant_id;
        this.category_id = other.category_id;
        this.item_name = other.item_name;
        this.item_quantity = other.item_quantity;
        this.item_description = other.item_description;
        this.item_price = other.item_price;
        this.item_discounted_price = other.item_discounted_price;
        this.item_preparation_time = other.item_preparation_time;
        this.is_available = other.is_available;
        this.item_image_url = other.item_image_url;
        this.item_display_order = other.item_display_order;
        this.item_created_at = other.item_created_at;
        this.item_updated_at = other.item_updated_at;   
    }

    // Getters and setters
    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(int item_quantity) {
        this.item_quantity = item_quantity;
    }

    public String getItem_description() {
        return item_description;
    }

    public void setItem_description(String item_description) {
        this.item_description = item_description;
    }

    public float getItem_price() {
        return item_price;
    }

    public void setItem_price(float item_price) {
        this.item_price = item_price;
    }

    public float getItem_discounted_price() {
        return item_discounted_price;
    }

    public void setItem_discounted_price(float item_discounted_price) {
        this.item_discounted_price = item_discounted_price;
    }

    public int getItem_preparation_time() {
        return item_preparation_time;
    }

    public void setItem_preparation_time(int item_preparation_time) {
        this.item_preparation_time = item_preparation_time;
    }

    public Boolean getIs_available() {
        return is_available;
    }

    public void setIs_available(Boolean is_available) {
        this.is_available = is_available;
    }

    public String getItem_image_url() {
        return item_image_url;
    }

    public void setItem_image_url(String item_image_url) {
        this.item_image_url = item_image_url;
    }

    public int getItem_display_order() {
        return item_display_order;
    }

    public void setItem_display_order(int item_display_order) {
        this.item_display_order = item_display_order;
    }

    public Timestamp getItem_created_at() {
        return item_created_at;
    }

    public void setItem_created_at(Timestamp item_created_at) {
        this.item_created_at = item_created_at;
    }

    public Timestamp getItem_updated_at() {
        return item_updated_at;
    }

    public void setItem_updated_at(Timestamp item_updated_at) {
        this.item_updated_at = item_updated_at;
    }

}
