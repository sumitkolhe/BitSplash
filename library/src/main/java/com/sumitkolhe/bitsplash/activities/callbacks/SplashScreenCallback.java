package com.sumitkolhe.bitsplash.activities.callbacks;

import androidx.annotation.NonNull;

import com.sumitkolhe.bitsplash.activities.configurations.SplashScreenConfiguration;

public interface SplashScreenCallback {

    @NonNull SplashScreenConfiguration onInit();
}
