package com.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OrderModel {

    private int id;
    private int customer_id;
    private int restaurant_id;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal discount_amount;
    private BigDecimal total_amount;
    private String payment_method;
    private String special_instructions;
    private Timestamp actual_delivery_time;
    private Timestamp created_at;
    private Timestamp updated_at;
    private Timestamp confirmed_at;
    private Timestamp prepared_at;
    private Timestamp picked_up_at;
    private Timestamp delivered_at;

    public OrderModel() {
    }

    public OrderModel(int id, int customer_id, int restaurant_id, String status,
            BigDecimal subtotal, BigDecimal discount_amount, BigDecimal total_amount,
            String payment_method, String special_instructions,
            Timestamp actual_delivery_time, Timestamp created_at, Timestamp updated_at,
            Timestamp confirmed_at, Timestamp prepared_at, Timestamp picked_up_at,
            Timestamp delivered_at) {
        this.id = id;
        this.customer_id = customer_id;
        this.restaurant_id = restaurant_id;
        this.status = status;
        this.subtotal = subtotal;
        this.discount_amount = discount_amount;
        this.total_amount = total_amount;
        this.payment_method = payment_method;
        this.special_instructions = special_instructions;
        this.actual_delivery_time = copyTimestamp(actual_delivery_time);
        this.created_at = copyTimestamp(created_at);
        this.updated_at = copyTimestamp(updated_at);
        this.confirmed_at = copyTimestamp(confirmed_at);
        this.prepared_at = copyTimestamp(prepared_at);
        this.picked_up_at = copyTimestamp(picked_up_at);
        this.delivered_at = copyTimestamp(delivered_at);
    }

    public OrderModel(OrderModel other) {
        this.id = other.id;
        this.customer_id = other.customer_id;
        this.restaurant_id = other.restaurant_id;
        this.status = other.status;
        this.subtotal = other.subtotal;
        this.discount_amount = other.discount_amount;
        this.total_amount = other.total_amount;
        this.payment_method = other.payment_method;
        this.special_instructions = other.special_instructions;
        this.actual_delivery_time = copyTimestamp(other.actual_delivery_time);
        this.created_at = copyTimestamp(other.created_at);
        this.updated_at = copyTimestamp(other.updated_at);
        this.confirmed_at = copyTimestamp(other.confirmed_at);
        this.prepared_at = copyTimestamp(other.prepared_at);
        this.picked_up_at = copyTimestamp(other.picked_up_at);
        this.delivered_at = copyTimestamp(other.delivered_at);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(BigDecimal discount_amount) {
        this.discount_amount = discount_amount;
    }

    public BigDecimal getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(BigDecimal total_amount) {
        this.total_amount = total_amount;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getSpecial_instructions() {
        return special_instructions;
    }

    public void setSpecial_instructions(String special_instructions) {
        this.special_instructions = special_instructions;
    }

    public Timestamp getActual_delivery_time() {
        return copyTimestamp(actual_delivery_time);
    }

    public void setActual_delivery_time(Timestamp actual_delivery_time) {
        this.actual_delivery_time = copyTimestamp(actual_delivery_time);
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

    public Timestamp getConfirmed_at() {
        return copyTimestamp(confirmed_at);
    }

    public void setConfirmed_at(Timestamp confirmed_at) {
        this.confirmed_at = copyTimestamp(confirmed_at);
    }

    public Timestamp getPrepared_at() {
        return copyTimestamp(prepared_at);
    }

    public void setPrepared_at(Timestamp prepared_at) {
        this.prepared_at = copyTimestamp(prepared_at);
    }

    public Timestamp getPicked_up_at() {
        return copyTimestamp(picked_up_at);
    }

    public void setPicked_up_at(Timestamp picked_up_at) {
        this.picked_up_at = copyTimestamp(picked_up_at);
    }

    public Timestamp getDelivered_at() {
        return copyTimestamp(delivered_at);
    }

    public void setDelivered_at(Timestamp delivered_at) {
        this.delivered_at = copyTimestamp(delivered_at);
    }

    private static Timestamp copyTimestamp(Timestamp value) {
        return value == null ? null : (Timestamp) value.clone();
    }
}
