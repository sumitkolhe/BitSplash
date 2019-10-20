package com.sumitkolhe.bitsplash.activities.callbacks;

import androidx.annotation.NonNull;

import com.sumitkolhe.bitsplash.activities.configurations.ActivityConfiguration;



public interface ActivityCallback {

    @NonNull ActivityConfiguration onInit();
}
