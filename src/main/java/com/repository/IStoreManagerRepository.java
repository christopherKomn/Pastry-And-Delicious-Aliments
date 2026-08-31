package com.repository;

import java.util.List;

import com.ErrorCodes;
import com.models.StoreManagerModel;

public interface IStoreManagerRepository  {

    

    StoreManagerModel findById(int id);

    List<StoreManagerModel> findAll();

    ErrorCodes save(StoreManagerModel storeManager);

    ErrorCodes update(StoreManagerModel storeManager);

    ErrorCodes deleteById(int id);

    StoreManagerModel findByOwnerId(int ownerId);

    StoreManagerModel findByNCAP(
        String name , String city, 
        String addressLine1, String postalCode);
        


}