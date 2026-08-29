package com.repository;

import java.util.List;

import com.models.StoreManagerModel;

public interface IStoreManagerRepository  {

    public enum ErrorType {
        NOT_FOUND,
        IO_ERROR,
        SUCCESS
    };

    StoreManagerModel findById(int id);

    List<StoreManagerModel> findAll();

    ErrorType save(StoreManagerModel storeManager);

    ErrorType update(StoreManagerModel storeManager);

    ErrorType deleteById(int id);

    StoreManagerModel findByOwnerId(int ownerId);

    StoreManagerModel findByNCAP(
        String name , String city, 
        String addressLine1, String postalCode);
        


}