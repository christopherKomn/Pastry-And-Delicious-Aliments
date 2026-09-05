package com.customer;

import java.sql.Connection;

import com.models.UserModel;

public class CustomerMain {
    public static void CMain(String[] args ,Connection dbConnection , UserModel user) {
        System.out.println("Hello, Customer!");

    }
}
