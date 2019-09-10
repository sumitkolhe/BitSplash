package com.sumitkolhe.bitsplash.activities;

import android.support.annotation.NonNull;

import com.sumitkolhe.bitsplash.activities.configurations.ActivityConfiguration;
import com.sumitkolhe.bitsplash.licenses.License;

public class MainActivity extends WallpaperBoardActivity {

    @NonNull
    @Override
    public ActivityConfiguration onInit() {
        return new ActivityConfiguration()
                .setLicenseCheckerEnabled(License.isLicenseCheckerEnabled())
                .setLicenseKey(License.getLicenseKey())
                .setRandomString(License.getRandomString())
                .setDonationProductsId(License.getDonationProductsId());
    }
}
