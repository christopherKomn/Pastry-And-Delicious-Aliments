package com.models;

public class CustomerModel {
    private int id;
    private int user_id;
    private String fullname;
    private String city;
    private String state;
    private String postal_code;
    private String address_line1;
    private String address_line2;

    public CustomerModel() {
    }

    public CustomerModel(int id, int user_id, String fullname, String city, String state,
            String postal_code, String address_line1, String address_line2) {
        this.id = id;
        this.user_id = user_id;
        this.fullname = fullname;
        this.city = city;
        this.state = state;
        this.postal_code = postal_code;
        this.address_line1 = address_line1;
        this.address_line2 = address_line2;
    }

    public CustomerModel(CustomerModel other) {
        this.id = other.id;
        this.user_id = other.user_id;
        this.fullname = other.fullname;
        this.city = other.city;
        this.state = other.state;
        this.postal_code = other.postal_code;
        this.address_line1 = other.address_line1;
        this.address_line2 = other.address_line2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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
}