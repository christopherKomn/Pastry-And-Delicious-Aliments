package com.repository;

import java.util.List;

import com.ErrorCodes;
import com.models.CustomerModel;
public interface ICustomerRepository  {
    CustomerModel findById(int id);

    List<CustomerModel> findAll();

    ErrorCodes save(CustomerModel customer);

    ErrorCodes update(CustomerModel customer);

    ErrorCodes deleteById(int id);
}