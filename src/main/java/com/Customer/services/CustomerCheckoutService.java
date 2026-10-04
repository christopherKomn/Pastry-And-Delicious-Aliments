package com.customer.services;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ErrorCodes;
import com.models.CartItem;
import com.models.CustomerModel;
import com.models.OrderItemsModel;
import com.models.OrderModel;
import com.repository.ICustomerRepository;
import com.repository.IOrderItemsRepository;
import com.repository.IOrderRepository;


public class CustomerCheckoutService {
    private static final Logger LOGGER =
            Logger.getLogger(CustomerCheckoutService.class.getName());

    private final Connection connection;
    private final ICustomerRepository customerRepository;
    private final IOrderRepository orderRepository;
    private final IOrderItemsRepository orderItemsRepository;

    public CustomerCheckoutService() {
        this(null, null, null, null);
    }

    public CustomerCheckoutService(
            Connection connection,
            ICustomerRepository customerRepository,
            IOrderRepository orderRepository,
            IOrderItemsRepository orderItemsRepository) {
        this.connection = connection;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
    }

    public boolean canOpenCheckout(List<CartItem> cartItems) {
        return cartItems != null && !cartItems.isEmpty();
    }

    public BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::calculateLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateLineTotal(CartItem cartItem) {
        return cartItem.getProduct().getItem_price()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }

    public ErrorCodes placeOrder(
            int userId,
            int restaurantId,
            List<CartItem> cartItems,
            String paymentMethod) {
        if (cartItems == null || cartItems.isEmpty()
                || paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Order details are incomplete.");
        }
        if (connection == null || customerRepository == null
                || orderRepository == null || orderItemsRepository == null) {
            return ErrorCodes.UNKNOWN_ERROR;
        }

        boolean previousAutoCommit = true;
        boolean transactionStarted = false;
        try {
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            transactionStarted = true;

            CustomerModel customer = customerRepository.findByUserId(userId);
            if (customer == null) {
                connection.rollback();
                return ErrorCodes.NOT_FOUND;
            }

            BigDecimal total = calculateCartTotal(cartItems);
            OrderModel order = new OrderModel();
            order.setCustomer_id(customer.getId());
            order.setRestaurant_id(restaurantId);
            order.setStatus("pending");
            order.setSubtotal(total);
            order.setDiscount_amount(BigDecimal.ZERO);
            order.setTotal_amount(total);
            order.setPayment_method(toDatabasePaymentMethod(paymentMethod));

            ErrorCodes result = orderRepository.save(order);
            if (result != ErrorCodes.SUCCESS) {
                connection.rollback();
                return result;
            }

            for (CartItem cartItem : cartItems) {
                OrderItemsModel orderItem = new OrderItemsModel();
                orderItem.setOrder_id(order.getId());
                orderItem.setMenu_item_id(cartItem.getProduct().getItem_id());
                orderItem.setQuantity(cartItem.getQuantity());
                result = orderItemsRepository.save(orderItem);
                if (result != ErrorCodes.SUCCESS) {
                    connection.rollback();
                    return result;
                }
            }

            connection.commit();
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            if (transactionStarted) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
            }
            LOGGER.log(Level.SEVERE, "Could not place customer order.", exception);
            return ErrorCodes.IO_ERROR;
        } finally {
            if (transactionStarted) {
                try {
                    connection.setAutoCommit(previousAutoCommit);
                } catch (SQLException exception) {
                    LOGGER.log(Level.SEVERE, "Could not restore database auto-commit.", exception);
                }
            }
        }
    }

    private String toDatabasePaymentMethod(String paymentMethod) {
        return switch (paymentMethod) {
            case "Cash" -> "cash";
            case "Card" -> "credit_card";
            default -> throw new IllegalArgumentException("Unsupported payment method.");
        };
    }
    /* 
    public void SetOrderToDB(List<CartItem> cartItems, Connection dbConnection, CustomerModel customer){

        Calendar calendarInstance = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(calendarInstance.getTime().getTime());

        // Please make these work while im gone
        //discount
        //subtotal
        //payment_method
        //special_instructions
        //actual_delivery_time
        try
        {
            Statement stmt = dbConnection.createStatement();
            
            // Inserting data in database
            String q1 = "insert into orders (customer_id,restaurant_id,status,subtotal,discount_amount,total_amount,payment_method,special_instructions,actual_delivery_time,created_at) values('" 
            +customer.getId()+ "', '" +1+ "','" +"pending"+ "', '" +subtotal+ "', '" +discount+ "', '" +payment_method+ "', '" +special_instructions+ "', '" +actual_delivery_time+ "', '" +timestamp+ "')";
            int x = stmt.executeUpdate(q1);
            if (x > 0)            
                System.out.println("Successfully Inserted");            
            else            
                System.out.println("Insert Failed");
            
            dbConnection.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }*/
}