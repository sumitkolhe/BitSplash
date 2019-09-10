package com.sumitkolhe.bitsplash.activities.callbacks;

import android.support.annotation.NonNull;

import com.sumitkolhe.bitsplash.activities.configurations.ActivityConfiguration;

/**
 * Author: Dani Mahardhika
 * Created on: 10/27/2017
 * https://github.com/danimahardhika
 */

public interface ActivityCallback {

    @NonNull ActivityConfiguration onInit();
}
