package com.customer.controllers;

import javax.swing.JOptionPane;

import com.customer.services.CustomerDeliveryDetailsService;
import com.customer.views.CustomerDeliveryDetailsView;

public class CustomerDeliveryDetailsController {
    private final CustomerDeliveryDetailsService service;
    private String city = "";
    private String address = "";
    private String postalCode = "";
    private String contactPhone = "";

    public CustomerDeliveryDetailsController(CustomerDeliveryDetailsService service) {
        this.service = service;
    }

    public void openDeliveryDetails(Runnable onSaved) {
        CustomerDeliveryDetailsView view = new CustomerDeliveryDetailsView();
        view.setDetails(city, address, postalCode, contactPhone);
        view.addSaveListener(event -> saveDetails(view, onSaved));
        view.setVisible(true);
    }

    private void saveDetails(CustomerDeliveryDetailsView view, Runnable onSaved) {
        boolean complete = service.areDetailsComplete(
                view.getCity(),
                view.getAddress(),
                view.getPostalCode(),
                view.getContactPhone());
        if (!complete) {
            JOptionPane.showMessageDialog(
                    view,
                    "Please complete all delivery details.",
                    "Missing details",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            city = view.getCity().trim();
            address = view.getAddress().trim();
            postalCode = view.getPostalCode().trim();
            contactPhone = view.getContactPhone().trim();
            view.dispose();
            if (onSaved != null) {
                onSaved.run();
            }
        }
    }

}
