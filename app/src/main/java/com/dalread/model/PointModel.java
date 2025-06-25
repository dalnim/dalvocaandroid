package com.dalread.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JetVHS on 7/7/17.
 */

public class PointModel {
    @SerializedName("POINT")
    private String point;
    @SerializedName("PRICE")
    private String price;
    @SerializedName("PRODUCT_ID")
    private String productId;

    public PointModel() {
    }

    public PointModel(String point, String price, String productId) {
        this.point = point;
        this.price = price;
        this.productId = productId;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "PointModel{" +
                "point='" + point + '\'' +
                ", price='" + price + '\'' +
                ", productId='" + productId + '\'' +
                '}';
    }
}
