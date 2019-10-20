package com.sumitkolhe.bitsplash.items;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;



public class Collection {

    private final int mIcon;
    private final Fragment mFragment;
    private final String mTag;

    public Collection(@DrawableRes int icon, @NonNull Fragment fragment, @NonNull String tag) {
        mIcon = icon;
        mFragment = fragment;
        mTag = tag;
    }

    @DrawableRes
    public int getIcon() {
        return mIcon;
    }

    @NonNull
    public Fragment getFragment() {
        return mFragment;
    }

    public String getTag() {
        return mTag;
    }
}
