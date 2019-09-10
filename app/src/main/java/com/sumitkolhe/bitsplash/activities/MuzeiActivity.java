package com.sumitkolhe.bitsplash.activities;

import android.support.annotation.NonNull;

import com.sumitkolhe.bitsplash.services.MuzeiService;

public class MuzeiActivity extends WallpaperBoardMuzeiActivity {

    @NonNull
    @Override
    public Class<?> onInit() {
        return MuzeiService.class;
    }
}
