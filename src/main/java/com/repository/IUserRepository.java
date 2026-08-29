package com.repository;

import java.util.List;

import com.models.UserModel;
public interface IUserRepository {

    public enum ErrorType {
        NOT_FOUND,
        IO_ERROR,
        SUCCESS
    };
    
    UserModel findUserById(int id);

    List<UserModel> findAllUsers();

    ErrorType saveUser(UserModel user);

    ErrorType updateUser(UserModel user);

    ErrorType deleteUserById(int id);

    UserModel findByUsername(String username);

    

    
};