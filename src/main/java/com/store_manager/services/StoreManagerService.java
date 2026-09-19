package com.store_manager.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ErrorCodes;
import com.models.CustomerModel;
import com.models.OrderModel;
import com.models.StoreManagerModel;
import com.repository.ICustomerRepository;
import com.repository.IOrderRepository;
import com.repository.IStoreManagerRepository;
public class StoreManagerService {

    private static final Logger LOGGER =
            Logger.getLogger(StoreManagerService.class.getName());

    private final StoreManagerModel m_storeManager;
    private final IStoreManagerRepository m_storeManagerRepository;
    private final IOrderRepository m_orderRepository;
    private final ICustomerRepository m_customerRepository;
    private int ownerId;

    public StoreManagerService(StoreManagerModel storeManager, IStoreManagerRepository storeManagerRepository,
                               IOrderRepository orderRepository, ICustomerRepository customerRepository) {
        if (storeManager == null || storeManagerRepository == null ||
            orderRepository == null || customerRepository == null) {
            LOGGER.severe(
                "[constructor] Cannot create StoreManagerService because of null parameter ."
            );

            throw new IllegalArgumentException(
                "A parameter is null"
            );
        }
        this.m_storeManager = storeManager;
        this.m_storeManagerRepository = storeManagerRepository;
        this.m_orderRepository = orderRepository;
        this.m_customerRepository = customerRepository;
        this.ownerId = storeManager.getOwner_id();
    }

    


    public StoreManagerService( StoreManagerService other) {
        this(
            other.m_storeManager,
            other.m_storeManagerRepository,
            other.m_orderRepository,
            other.m_customerRepository
        );
    }


    // getters
    public StoreManagerModel getStoreManager() {
        return m_storeManager;
    }

    public IStoreManagerRepository getStoreManagerRepository() {
        return m_storeManagerRepository;
    }

    public IOrderRepository getOrderRepository() {
        return m_orderRepository;
    }

    public ICustomerRepository getCustomerRepository() {
        return m_customerRepository;
    }

    /**
     * @brief Returns a list of customers with orders on the Store 
     * manager given in contructor . Null if no order found .
     */
    public List<CustomerModel> getAllCustomersOrders(){
        List<OrderModel> orders = m_orderRepository.findByRestaurantId(
            Long.valueOf(m_storeManager.getRestaurant_id())
        );
        if (orders == null) return null;

        List<CustomerModel> customers = new ArrayList<>();
        for (OrderModel order : orders) {
            CustomerModel customer = 
            m_customerRepository.findById(order.getCustomer_id());
            if ( (customer != null)  ) {
                customers.add(customer);
            }
        }
        if (customers.isEmpty()){
            
            return null;
        }

        
        return customers; 
    }

    /**
     * @brief Returns if exists an order model of this customer for 
     * the store manager given in the constructor .
     * @param customer  The customer model .
     * @returns Null if there is no order of this customer from the given 
     * StoreManagerModel , otherwise returns the Order Model that represents 
     * the actuall order .
     */
    public OrderModel getOrderByCustomer(CustomerModel customer) {
        if (customer == null) {
            throw new IllegalArgumentException(
                "customer parameter is null"
            );
        }


        List<OrderModel> orders = m_orderRepository.findByRestaurantId(
            Long.valueOf(m_storeManager.getRestaurant_id())
        );

        for (OrderModel order : orders) {
            if (order.getCustomer_id() == customer.getId()) {
                return order;
            }
        }

        return null; 
    }

    /**
     * @brief deletes the hole store and that means the store manager from the 
     * data base .
     * @returns ErrorCodes 
     *  1. SUCCESS if deleted succefully 
     *  2. NOT_FOUND If there is no valid store
     *  3. IO_ERROR for anything else 
     */
    public ErrorCodes deleteStore() {
        return m_storeManagerRepository.deleteByOwnerId(ownerId);
    }


    
}
