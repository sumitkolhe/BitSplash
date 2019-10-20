package com.sumitkolhe.bitsplash.items;


public class InAppBilling {

    private final String mProductId;
    private String mProductName;
    private String mPrice;

    public InAppBilling(String productId) {
        mProductId = productId;
    }

    public InAppBilling(String price, String productId, String productName) {
        mPrice = price;
        mProductId = productId;
        mProductName = productName;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getProductId() {
        return mProductId;
    }

    public String getProductName() {
        return mProductName;
    }

}
