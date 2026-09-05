package com.store_manager.services;

import com.models.MenuItemsModel;
import com.models.OrderItemsModel;
import com.models.StoreManagerModel;

import java.util.*;

public class StoreManagerService {
    /*
    // Mock data storage
    private Map<Integer, Store> stores = new HashMap<>();
    private Map<Integer, List<MenuItem>> storeMenuItems = new HashMap<>();
    private Map<Integer, List<Order>> storeOrders = new HashMap<>();
    private int menuItemCounter = 100;
    private int orderCounter = 1000;
    
    public StoreManagerService() {
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Sample Store
        Store store = new Store(1, "Tech World", "Electronics and Gadgets", 
                "123 Tech Street, Silicon Valley", "555-0100", 
                "info@techworld.com", "OPENED", 101);
        stores.put(1, store);
        
        // Sample Menu Items
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(1, 1, "Laptop Pro", "High-end laptop", 1299.99, true, 15));
        menuItems.add(new MenuItem(2, 1, "Wireless Mouse", "Ergonomic wireless mouse", 29.99, true, 5));
        menuItems.add(new MenuItem(3, 1, "USB-C Hub", "7-in-1 USB-C hub", 49.99, true, 10));
        menuItems.add(new MenuItem(4, 1, "External SSD", "1TB External SSD", 159.99, false, 20));
        menuItems.add(new MenuItem(5, 1, "Bluetooth Headphones", "Noise-cancelling headphones", 199.99, true, 25));
        storeMenuItems.put(1, menuItems);
        menuItemCounter = 6;
        
        // Sample Orders
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order(1001, 1, 201, new Date(), 1359.97, "PENDING", 
                "123 Customer St, City", "John Doe");
        order1.addItem(new OrderItem(1, "Laptop Pro", 1, 1299.99, 1299.99, "Need Windows 11"));
        order1.addItem(new OrderItem(2, "Wireless Mouse", 2, 29.99, 59.98, null));
        orders.add(order1);
        
        Order order2 = new Order(1002, 1, 202, new Date(), 249.98, "PENDING", 
                "456 Buyer Ave, Town", "Jane Smith");
        order2.addItem(new OrderItem(3, "USB-C Hub", 2, 49.99, 99.98, null));
        order2.addItem(new OrderItem(5, "Bluetooth Headphones", 1, 199.99, 199.99, "White color"));
        orders.add(order2);
        
        Order order3 = new Order(1003, 1, 203, new Date(), 29.99, "ACCEPTED", 
                "789 Client Rd, Village", "Bob Johnson");
        order3.addItem(new OrderItem(2, "Wireless Mouse", 1, 29.99, 29.99, null));
        orders.add(order3);
        
        Order order4 = new Order(1004, 1, 204, new Date(), 49.99, "REJECTED", 
                "321 Shopper Ln, County", "Alice Brown");
        order4.addItem(new OrderItem(3, "USB-C Hub", 1, 49.99, 49.99, null));
        orders.add(order4);
        
        Order order5 = new Order(1005, 1, 205, new Date(), 159.99, "REPORTED", 
                "654 Consumer Blvd, City", "Charlie Wilson");
        order5.addItem(new OrderItem(4, "External SSD", 1, 159.99, 159.99, null));
        orders.add(order5);
        
        storeOrders.put(1, orders);
        orderCounter = 1006;
    }
    
    // ============ STORE STATUS MANAGEMENT ============
    
    public boolean updateStoreStatus(int storeId, String newStatus) {
        Store store = stores.get(storeId);
        if (store == null) {
            System.out.println("[DATABASE] Store not found with ID: " + storeId);
            return false;
        }
        
        String oldStatus = store.getStatus();
        store.setStatus(newStatus);
        System.out.println("[DATABASE] Store status updated from " + oldStatus + " to " + newStatus);
        return true;
    }
    
    // ============ MENU ITEM MANAGEMENT ============
    
    public boolean createMenuItem(int storeId, String name, String description, 
                                  double price, int preparationTime) {
        Store store = stores.get(storeId);
        if (store == null) {
            System.out.println("[DATABASE] Store not found with ID: " + storeId);
            return false;
        }
        
        MenuItem item = new MenuItem(menuItemCounter++, storeId, name, description, price, true, preparationTime);
        storeMenuItems.computeIfAbsent(storeId, k -> new ArrayList<>()).add(item);
        
        System.out.println("[DATABASE] Menu item created: " + name + " (ID: " + item.getItemId() + ")");
        return true;
    }
    
    public boolean removeMenuItem(int storeId, int itemId) {
        List<MenuItem> items = storeMenuItems.get(storeId);
        if (items == null) {
            System.out.println("[DATABASE] No menu items found for store ID: " + storeId);
            return false;
        }
        
        boolean removed = items.removeIf(item -> item.getItemId() == itemId);
        if (removed) {
            System.out.println("[DATABASE] Menu item removed (ID: " + itemId + ")");
        } else {
            System.out.println("[DATABASE] Menu item not found (ID: " + itemId + ")");
        }
        return removed;
    }
    
    public boolean toggleItemAvailability(int storeId, int itemId) {
        List<MenuItem> items = storeMenuItems.get(storeId);
        if (items == null) {
            System.out.println("[DATABASE] No menu items found for store ID: " + storeId);
            return false;
        }
        
        for (MenuItem item : items) {
            if (item.getItemId() == itemId) {
                boolean newStatus = !item.isAvailable();
                item.setAvailable(newStatus);
                System.out.println("[DATABASE] Item availability toggled to: " + (newStatus ? "AVAILABLE" : "UNAVAILABLE"));
                return true;
            }
        }
        
        System.out.println("[DATABASE] Menu item not found (ID: " + itemId + ")");
        return false;
    }
    
    public boolean updateItemPrice(int storeId, int itemId, double newPrice) {
        List<MenuItem> items = storeMenuItems.get(storeId);
        if (items == null) {
            System.out.println("[DATABASE] No menu items found for store ID: " + storeId);
            return false;
        }
        
        for (MenuItem item : items) {
            if (item.getItemId() == itemId) {
                double oldPrice = item.getPrice();
                item.setPrice(newPrice);
                System.out.println("[DATABASE] Item price updated from $" + oldPrice + " to $" + newPrice);
                return true;
            }
        }
        
        System.out.println("[DATABASE] Menu item not found (ID: " + itemId + ")");
        return false;
    }
    
    public List<MenuItem> getMenuItems(int storeId) {
        return storeMenuItems.getOrDefault(storeId, new ArrayList<>());
    }
    
    public MenuItem getMenuItem(int storeId, int itemId) {
        List<MenuItem> items = storeMenuItems.get(storeId);
        if (items == null) return null;
        
        for (MenuItem item : items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }
    
    // ============ ORDER MANAGEMENT ============
    
    public boolean validateOrder(int orderId, String action) {
        List<Order> orders = storeOrders.get(1); // Assuming store ID 1 for now
        if (orders == null) {
            System.out.println("[DATABASE] No orders found");
            return false;
        }
        
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                String oldStatus = order.getStatus();
                
                switch (action.toUpperCase()) {
                    case "ACCEPT":
                        order.setStatus("ACCEPTED");
                        System.out.println("[DATABASE] Order " + orderId + " ACCEPTED");
                        break;
                    case "REJECT":
                        order.setStatus("REJECTED");
                        System.out.println("[DATABASE] Order " + orderId + " REJECTED");
                        break;
                    case "REPORT":
                        order.setStatus("REPORTED");
                        System.out.println("[DATABASE] Order " + orderId + " REPORTED");
                        break;
                    default:
                        System.out.println("[DATABASE] Invalid action: " + action);
                        return false;
                }
                
                System.out.println("[DATABASE] Order status changed from " + oldStatus + " to " + order.getStatus());
                return true;
            }
        }
        
        System.out.println("[DATABASE] Order not found (ID: " + orderId + ")");
        return false;
    }
    
    public boolean updateOrderInformation(int orderId, String customerName, 
                                          String shippingAddress, List<OrderItem> newItems) {
        List<Order> orders = storeOrders.get(1); // Assuming store ID 1 for now
        if (orders == null) {
            System.out.println("[DATABASE] No orders found");
            return false;
        }
        
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                if (customerName != null && !customerName.isEmpty()) {
                    order.setCustomerName(customerName);
                }
                if (shippingAddress != null && !shippingAddress.isEmpty()) {
                    order.setShippingAddress(shippingAddress);
                }
                if (newItems != null && !newItems.isEmpty()) {
                    order.setItems(newItems);
                    // Recalculate total
                    double total = 0;
                    for (OrderItem item : newItems) {
                        total += item.getTotalPrice();
                    }
                    order.setTotalAmount(total);
                }
                
                System.out.println("[DATABASE] Order " + orderId + " information updated");
                return true;
            }
        }
        
        System.out.println("[DATABASE] Order not found (ID: " + orderId + ")");
        return false;
    }
    
    public boolean cancelOrder(int orderId) {
        List<Order> orders = storeOrders.get(1); // Assuming store ID 1 for now
        if (orders == null) {
            System.out.println("[DATABASE] No orders found");
            return false;
        }
        
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                order.setStatus("CANCELLED");
                System.out.println("[DATABASE] Order " + orderId + " CANCELLED");
                return true;
            }
        }
        
        System.out.println("[DATABASE] Order not found (ID: " + orderId + ")");
        return false;
    }
    
    public List<Order> getOrders() {
        return storeOrders.getOrDefault(1, new ArrayList<>());
    }
    
    public Order getOrder(int orderId) {
        List<Order> orders = storeOrders.get(1);
        if (orders == null) return null;
        
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        return null;
    }
    
    // ============ REMOVE STORE PAGE ============
    
    public boolean removeStorePage(int storeId) {
        Store store = stores.remove(storeId);
        if (store == null) {
            System.out.println("[DATABASE] Store not found with ID: " + storeId);
            return false;
        }
        
        storeMenuItems.remove(storeId);
        storeOrders.remove(storeId);
        
        System.out.println("[DATABASE] Store " + storeId + " removed with all associated data");
        return true;
    }
    
    public Store getStore(int storeId) {
        return stores.get(storeId);
    }*/
}