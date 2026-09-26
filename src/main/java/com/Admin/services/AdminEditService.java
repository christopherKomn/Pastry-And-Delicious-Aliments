package com.admin.services;

import com.ErrorCodes;
import com.models.UserModel;
import com.repository.IUserRepository;



public class AdminEditService {
    private final IUserRepository userRepo;
    
    public AdminEditService(IUserRepository userRepo){
        this.userRepo = userRepo;
    }

    public ErrorCodes updateProfile(UserModel adminUser){
        if (adminUser == null){
            throw new IllegalArgumentException("adminUser is null");
        }

        return userRepo.updateUser(adminUser);
    }
}
