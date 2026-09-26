package com.admin.controllers;

import com.admin.services.AdminEditService;
import com.admin.views.EditUserView;
import com.models.UserModel;
    

public class AdminEditController {
    private final EditUserView view;
    private final AdminEditService service;
    private UserModel user;
    public AdminEditController(
        UserModel user ,
        EditUserView view ,
        AdminEditService service
    ){
        this.view = view;
        this.user = user;
        this.service = service ;
        InitController();
    }

    public void InitController(){
        view.setUser(user);
        view.addSaveListener(event -> {
            UserModel editedUser = view.getUser();
            service.updateProfile(editedUser);
            user = editedUser;
        });
    }
}
