package com.sumitkolhe.bitsplash.activities;

import android.support.annotation.NonNull;

import com.sumitkolhe.bitsplash.activities.configurations.SplashScreenConfiguration;
import com.sumitkolhe.bitsplash.board.sample.R;

public class SplashActivity extends WallpaperBoardSplashActivity {

    @NonNull
    @Override
    public SplashScreenConfiguration onInit() {
        return new SplashScreenConfiguration(MainActivity.class)
                .setBottomText(getString(R.string.splash_screen_title));
    }
}
