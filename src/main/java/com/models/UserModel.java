package com.models;

import java.sql.Timestamp;
public class UserModel {

    private int user_id;
    private String user_email;
    private String user_phone;
    private String user_username;
    private String user_password;
    private String user_type;
    private String user_profile_image_url;
    private Timestamp user_created_at;

    // Constructors
    public UserModel() {
    }

    public UserModel(int id, String email, String phone, String username, String password,
                     String user_type, String profile_image_url, Timestamp created_at) {
        this.user_id = id;
        this.user_email = email;
        this.user_phone = phone;
        this.user_username = username;
        this.user_password = password;
        this.user_type = user_type;
        this.user_profile_image_url = profile_image_url;
        this.user_created_at = created_at;
    }

    public UserModel(UserModel other) {
        this.user_id = other.user_id;
        this.user_email = other.user_email;
        this.user_phone = other.user_phone;
        this.user_username = other.user_username;
        this.user_password = other.user_password;
        this.user_type = other.user_type;
        this.user_profile_image_url = other.user_profile_image_url;
        this.user_created_at = other.user_created_at;
    }

    // Getters and setters
    public int getUserId() {
        return user_id;
    }

    public void setUserId(int id) {
        this.user_id = id;
    }

    public String getUserEmail() {
        return user_email;
    }

    public void setUserEmail(String email) {
        this.user_email = email;
    }

    public String getUserPhone() {
        return user_phone;
    }

    public void setUserPhone(String phone) {
        this.user_phone = phone;
    }

    public String getUsername() {
        return user_username;
    }

    public void setUsername(String username) {
        this.user_username = username;
    }

    public String getUserPassword() {
        return user_password;
    }

    public void setUserPassword(String password) {
        this.user_password = password;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_profile_image_url() {
        return user_profile_image_url;
    }

    public void setUser_profile_image_url(String profile_image_url) {
        this.user_profile_image_url = profile_image_url;
    }

    public Timestamp getUser_created_at() {
        return user_created_at;
    }

    public void setUser_created_at(Timestamp created_at) {
        this.user_created_at = created_at;
    }
}
