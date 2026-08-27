package com.repository;

import java.util.List;

import com.models.UserModel;
public interface IUserRepository {
    
    UserModel findUserById(int id);

    List<UserModel> findAllUsers();

    void saveUser(UserModel user);

    void updateUser(UserModel user);

    void deleteUserById(int id);

    
};