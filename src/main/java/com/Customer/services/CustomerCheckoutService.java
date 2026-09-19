package com.customer.services;


import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;
import java.util.List;

import com.models.CartItem;
import com.models.CustomerModel;


public class CustomerCheckoutService {
    public boolean canOpenCheckout(List<CartItem> cartItems) {
        return cartItems != null && !cartItems.isEmpty();
    }

    public BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::calculateLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateLineTotal(CartItem cartItem) {
        return cartItem.getProduct().getItem_price()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }

    public void SetOrderToDB(List<CartItem> cartItems, Connection dbConnection, CustomerModel customer){

        Calendar calendarInstance = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(calendarInstance.getTime().getTime());

        // Please make these work while im gone
        //discount
        //subtotal
        //payment_method
        //special_instructions
        //actual_delivery_time
        try
        {
            Statement stmt = dbConnection.createStatement();
            
            // Inserting data in database
            String q1 = "insert into orders (customer_id,restaurant_id,status,subtotal,discount_amount,total_amount,payment_method,special_instructions,actual_delivery_time,created_at) values('" +customer.getId()+ "', '" +1+ "','" +"pending"+ "', '" +subtotal+ "', '" +discount+ "', '" +payment_method+ "', '" +special_instructions+ "', '" +actual_delivery_time+ "', '" +timestamp+ "')";
            int x = stmt.executeUpdate(q1);
            if (x > 0)            
                System.out.println("Successfully Inserted");            
            else            
                System.out.println("Insert Failed");
            
            dbConnection.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}