package com.sumitkolhe.bitsplash.utils.listeners;

import com.sumitkolhe.bitsplash.items.InAppBilling;


public interface InAppBillingListener {

    void onInAppBillingSelected(InAppBilling product);
    void onInAppBillingConsume(String productId);

}
