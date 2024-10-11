package com.moutamid.fallsdelivery.models;

public class OrderModel {
    public String uid, cartID, name, phone, address, latitude, longitude;
    public ProductModel productModel;
    public int quantity;
    public String proof;
    public boolean COD;

    public OrderModel() {
    }
}
