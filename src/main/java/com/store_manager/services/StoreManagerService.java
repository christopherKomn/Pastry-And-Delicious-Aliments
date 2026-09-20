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

    private final IStoreManagerRepository m_storeManagerRepository;
    private final IOrderRepository m_orderRepository;
    private final ICustomerRepository m_customerRepository;

    /**
     * @brief Constructor that takes the repos interface necessery to do the services .
     * @throws IllegalArgumentException If one of the parameters is null.
     */
    public StoreManagerService( IStoreManagerRepository storeManagerRepository,
                               IOrderRepository orderRepository, ICustomerRepository customerRepository) {
        if (storeManagerRepository == null ||
            orderRepository == null || customerRepository == null) {
            LOGGER.severe(
                "[constructor] Cannot create StoreManagerService because of null parameter ."
            );

            throw new IllegalArgumentException(
                "A parameter is null"
            );
        }
        this.m_storeManagerRepository = storeManagerRepository;
        this.m_orderRepository = orderRepository;
        this.m_customerRepository = customerRepository;
    }

    


    public StoreManagerService( StoreManagerService other) {
        this(
            other.m_storeManagerRepository,
            other.m_orderRepository,
            other.m_customerRepository
        );
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
     * @param store The Store to retreive the orders .
     * @throws IllegalArgumentException If store is null.
     */
    public List<CustomerModel> getAllCustomersOrders(StoreManagerModel store){
        if (store == null){
            throw new IllegalArgumentException(
                "store parameter is null"
            );
        }
        List<OrderModel> orders = m_orderRepository.findByRestaurantId(
            Long.valueOf(store.getRestaurant_id())
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
            LOGGER.warning("No Orders from customers found !");
            return null;
        }

        LOGGER.info(() -> "Retrieved "
                    + customers.size() + " customers with orders .");
        return customers; 
    }

    /**
     * @brief Based on store and customer , retreives if exist's the order
     * this customer have done to this store  .
     * @param customer  The customer model .
     * @param store The Store model 
     * @returns OrderModel if there is a order from this customer on this store or 
     * null if there is not or any io error .
     * @throws IllegalArgumentException If store or customer parameters are null .
     * @note Returns the first matching order meaning if the custoemer has more orders
     * for the same store they will not show up .
     */
    public OrderModel getOrderByCustomer(StoreManagerModel store , CustomerModel customer) {
        if (customer == null || store == null) {
            throw new IllegalArgumentException(
                "customer and / or store parameter is null"
            );
        }


        List<OrderModel> orders = m_orderRepository.findByRestaurantId(
            Long.valueOf(store.getRestaurant_id())
        );

        for (OrderModel order : orders) {
            if (order.getCustomer_id() == customer.getId()) {
                LOGGER.info(()-> "Orders from customer \"" + customer + "\" found for store \"" + store + "\" .");
                return order;
            }
        }

        LOGGER.warning(()-> "No Orders from customer \"" + customer + "\" found for store \"" + store + "\" ");
        return null; 
    }

    /**
     * @brief deletes the hole store and that means the store manager from the 
     * data base (restaurant).
     * @param store The store manager model that represents the actual store to be deleted .
     * @returns ErrorCodes 
     *  1. SUCCESS if deleted succefully 
     *  2. NOT_FOUND If there is no valid store
     *  3. IO_ERROR for anything else 
     * @throws IllegalArgumentException If store is null .
     */
    public ErrorCodes deleteStore(StoreManagerModel store ) {
        if (store == null){
            throw new IllegalArgumentException(
                " store parameter is null"
            );
        }
        ErrorCodes res = 
         m_storeManagerRepository.deleteByOwnerId(store.getRestaurant_id());

        
        switch (res) {
            case SUCCESS:
                LOGGER.info(()-> "Store \"" + store + "\" deleted .");
                break;
            case NOT_FOUND:
                LOGGER.warning(()-> "Store \"" + store + "\"  Not found .");
                break;
            default:
                LOGGER.warning(()-> "Store \"" + store + "\"  failed to delete .");
                break;
        }
        return res;
    }


    
}
