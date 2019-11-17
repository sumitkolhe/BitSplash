package com.sumitkolhe.bitsplash.activities;

import android.content.Intent;

import androidx.annotation.NonNull;
import com.sumitkolhe.android.inapp.update.InAppUpdateStatus;
import com.sumitkolhe.bitsplash.activities.configurations.ActivityConfiguration;
import com.sumitkolhe.bitsplash.board.sample.R;
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


    @Override
    public void onInAppUpdateError(int i, Throwable throwable) {

    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus inAppUpdateStatus) {

    }
}
