package com.admin.controllers;

import com.admin.views.AdminInfoView;
import com.models.UserModel;

public class AdminInfoController {
    private AdminInfoView adminInfoView;
    private UserModel admin;
    public AdminInfoController(AdminInfoView adminInfoView , UserModel admin) {
        this.adminInfoView = adminInfoView;
        this.admin = admin;

        adminInfoView.addViewShownListener(event -> {
            adminInfoView.setUser(admin);
        });
    }

}     