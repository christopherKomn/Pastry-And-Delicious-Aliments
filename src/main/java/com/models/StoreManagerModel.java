package com.models;

import java.sql.Timestamp;
public class StoreManagerModel {
    
    private int restaurant_id;
    private int owner_id;
    private String name;
    private String description;
    private String logo_url;
    private String cover_image_url;
    private String cuisine_type;
    private String phone;
    private String email;
    private String address_line1;
    private String address_line2;
    private String city;
    private String state;
    private String postal_code;
    private String website;
    private boolean is_active;
    private boolean is_accepting_orders;
    private double min_order_amount;
    private double delivery_fee;
    private double rating;
    private int total_reviews;
    private Timestamp created_at;
    private Timestamp updated_at;

    // Constructors
    public StoreManagerModel() {
    }


    public StoreManagerModel(int restaurant_id, int owner_id, String name, String description, String logo_url,
            String cover_image_url, String cuisine_type, String phone, String email, String address_line1,
            String address_line2, String city, String state, String postal_code, String website, boolean is_active,
            boolean is_accepting_orders, double min_order_amount, double delivery_fee, double rating,
            int total_reviews, Timestamp created_at, Timestamp updated_at) {
        
        this.restaurant_id = restaurant_id;

        this.owner_id = owner_id;
        this.name = name;
        this.description = description;
        this.logo_url = logo_url;
        this.cover_image_url = cover_image_url;
        this.cuisine_type = cuisine_type;
        this.phone = phone;
        this.email = email;
        this.address_line1 = address_line1;
        this.address_line2 = address_line2;
        this.city = city;
        this.state = state;
        this.postal_code = postal_code;
        this.website = website;
        this.is_active = is_active;
        this.is_accepting_orders = is_accepting_orders;
        this.min_order_amount = min_order_amount;
        this.delivery_fee = delivery_fee;
        this.rating = rating;
        this.total_reviews = total_reviews;
        this.created_at = copyTimestamp(created_at);
        this.updated_at = copyTimestamp(updated_at);
    }

    public StoreManagerModel(StoreManagerModel other) {
        
        this.restaurant_id = other.restaurant_id;
        this.owner_id = other.owner_id;
        this.name = other.name;
        this.description = other.description;
        this.logo_url = other.logo_url;
        this.cover_image_url = other.cover_image_url;
        this.cuisine_type = other.cuisine_type;
        this.phone = other.phone;
        this.email = other.email;
        this.address_line1 = other.address_line1;
        this.address_line2 = other.address_line2;
        this.city = other.city;
        this.state = other.state;
        this.postal_code = other.postal_code;
        this.website = other.website;
        this.is_active = other.is_active;
        this.is_accepting_orders = other.is_accepting_orders;
        this.min_order_amount = other.min_order_amount;
        this.delivery_fee = other.delivery_fee;
        this.rating = other.rating;
        this.total_reviews = other.total_reviews;
        this.created_at = copyTimestamp(other.created_at);
        this.updated_at = copyTimestamp(other.updated_at);
    }

    // Getters and Setters
    
    
    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public void setCover_image_url(String cover_image_url) {
        this.cover_image_url = cover_image_url;
    }

    public String getCuisine_type() {
        return cuisine_type;
    }

    public void setCuisine_type(String cuisine_type) {
        this.cuisine_type = cuisine_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress_line1() {
        return address_line1;
    }

    public void setAddress_line1(String address_line1) {
        this.address_line1 = address_line1;
    }

    public String getAddress_line2() {
        return address_line2;
    }

    public void setAddress_line2(String address_line2) {
        this.address_line2 = address_line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public boolean isIs_accepting_orders() {
        return is_accepting_orders;
    }

    public void setIs_accepting_orders(boolean is_accepting_orders) {
        this.is_accepting_orders = is_accepting_orders;
    }

    public double getMin_order_amount() {
        return min_order_amount;
    }

    public void setMin_order_amount(double min_order_amount) {
        this.min_order_amount = min_order_amount;
    }

    public double getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(double delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotal_reviews() {
        return total_reviews;
    }

    public void setTotal_reviews(int total_reviews) {
        this.total_reviews = total_reviews;
    }

    public Timestamp getCreated_at() {
        return copyTimestamp(created_at);
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = copyTimestamp(created_at);
    }

    public Timestamp getUpdated_at() {
        return copyTimestamp(updated_at);
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = copyTimestamp(updated_at);
    }

    private static Timestamp copyTimestamp(Timestamp value) {
        return value == null ? null : (Timestamp) value.clone();
    }
}
