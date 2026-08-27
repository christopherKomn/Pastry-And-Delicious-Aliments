package com.repository;

import java.util.List;
import com.models.*;

public interface ICustomerRepository  {
    CustomerModel findById(int id);

    List<CustomerModel> findAll();

    void save(CustomerModel customer);

    void update(CustomerModel customer);

    void deleteById(int id);
}