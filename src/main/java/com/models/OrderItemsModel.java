package com.models;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class OrderItemsModel {

private int id;
    private int order_id;
    private int menu_item_id;
    private String item_name;
    private int quantity;
    private float unit_price;
    private float total_price;
    private String special_instructions;
    private String selected_options;
    private Map<String, Object> selectedOptionsMap;
    private Timestamp created_at;
    private Timestamp updated_at;

    public OrderItemsModel() {
        this.selectedOptionsMap = new HashMap<>();
    }

    public OrderItemsModel(int id, int order_id, int menu_item_id, String item_name, 
                    int quantity, float unit_price, float total_price, 
                    String special_instructions, String selected_options) {
        this.id = id;
        this.order_id = order_id;
        this.menu_item_id = menu_item_id;
        this.item_name = item_name;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.total_price = total_price;
        this.special_instructions = special_instructions;
        this.selected_options = selected_options;
        this.selectedOptionsMap = new HashMap<>();
    }

    public OrderItemsModel(OrderItemsModel other) {
        this.id = other.id;
        this.order_id = other.order_id;
        this.menu_item_id =other. menu_item_id;
        this.item_name = other.item_name;
        this.quantity = other.quantity;
        this.unit_price = other.unit_price;
        this.total_price = other.total_price;
        this.special_instructions = other.special_instructions;
        this.selected_options = other.selected_options;
        this.selectedOptionsMap = other.selectedOptionsMap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getMenu_item_id() {
        return menu_item_id;
    }

    public void setMenu_item_id(int menu_item_id) {
        this.menu_item_id = menu_item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(float unit_price) {
        this.unit_price = unit_price;
    }

    public float getTotal_price() {
        return total_price;
    }

    public void setTotal_price(float total_price) {
        this.total_price = total_price;
    }

    public String getSpecial_instructions() {
        return special_instructions;
    }

    public void setSpecial_instructions(String special_instructions) {
        this.special_instructions = special_instructions;
    }

    public String getSelected_options() {
        return selected_options;
    }

    public void setSelected_options(String selected_options) {
        this.selected_options = selected_options;
        // Parse JSON string to Map when set
        if (selected_options != null && !selected_options.isEmpty()) {
            this.selectedOptionsMap = parseJsonToMap(selected_options);
        }
    }

    public Map<String, Object> getSelectedOptionsMap() {
        return selectedOptionsMap;
    }

    public void setSelectedOptionsMap(Map<String, Object> selectedOptionsMap) {
        this.selectedOptionsMap = selectedOptionsMap;
        // Convert Map to JSON string
        if (selectedOptionsMap != null && !selectedOptionsMap.isEmpty()) {
            this.selected_options = mapToJsonString(selectedOptionsMap);
        }
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }


     private Map<String, Object> parseJsonToMap(String json) {
        Map<String, Object> map = new HashMap<>();
        // Simplified parsing - in production use proper JSON parser
        try {
            // Remove curly braces
            String clean = json.trim();
            if (clean.startsWith("{") && clean.endsWith("}")) {
                clean = clean.substring(1, clean.length() - 1);
                String[] pairs = clean.split(",");
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().replace("\"", "");
                        String value = keyValue[1].trim().replace("\"", "");
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            // If parsing fails, return empty map
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
        return map;
    }

    private String mapToJsonString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        
        StringBuilder json = new StringBuilder("{");
        int count = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (count > 0) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof Number) {
                json.append(value);
            } else if (value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(value).append("\"");
            }
            count++;
        }
        json.append("}");
        return json.toString();
    }

    public void addSelectedOption(String key, Object value) {
        if (selectedOptionsMap == null) {
            selectedOptionsMap = new HashMap<>();
        }
        selectedOptionsMap.put(key, value);
        // Update JSON string
        this.selected_options = mapToJsonString(selectedOptionsMap);
    }


    public Object getSelectedOption(String key) {
        if (selectedOptionsMap == null) {
            return null;
        }
        return selectedOptionsMap.get(key);
    }


    public void removeSelectedOption(String key) {
        if (selectedOptionsMap != null) {
            selectedOptionsMap.remove(key);
            this.selected_options = mapToJsonString(selectedOptionsMap);
        }
    }

    public boolean hasSelectedOption(String key) {
        return selectedOptionsMap != null && selectedOptionsMap.containsKey(key);
    }

}
