package com.repository;

import java.util.List;
import com.models.*;

public interface IStoreManagerRepository  {
    StoreManagerModel findById(int id);

    List<StoreManagerModel> findAll();

    void save(StoreManagerModel storeManager);

    void update(StoreManagerModel storeManager);

    void deleteById(int id);
}