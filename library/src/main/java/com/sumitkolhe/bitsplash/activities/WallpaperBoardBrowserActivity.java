package com.sumitkolhe.bitsplash.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.SoftKeyboardHelper;
import com.danimahardhika.android.helpers.core.WindowHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.fragments.CategoryWallpapersFragment;
import com.sumitkolhe.bitsplash.fragments.WallpaperSearchFragment;
import com.sumitkolhe.bitsplash.helpers.LocaleHelper;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.utils.Extras;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class WallpaperBoardBrowserActivity extends AppCompatActivity {

    private FragmentManager mFragManager;

    private int mFragmentId;
    private int mCategoryCount;
    private String mCategoryName;
    private String mFragmentTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.setTheme(Preferences.get(this).isDarkTheme() ?
                R.style.BrowserThemeDark : R.style.BrowserTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_browser);
        ButterKnife.bind(this);

        int color = ColorHelper.getAttributeColor(this, R.attr.colorPrimary);
        ColorHelper.setupStatusBarIconColor(this, ColorHelper.isLightColor(color));

        WindowHelper.resetNavigationBarTranslucent(this,
                WindowHelper.NavigationBarTranslucent.PORTRAIT_ONLY);

        SoftKeyboardHelper softKeyboardHelper = new SoftKeyboardHelper(this,
                findViewById(R.id.container));
        softKeyboardHelper.enable();

        mFragManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            mFragmentId = savedInstanceState.getInt(Extras.EXTRA_FRAGMENT_ID);
            mCategoryName = savedInstanceState.getString(Extras.EXTRA_CATEGORY);
            mCategoryCount = savedInstanceState.getInt(Extras.EXTRA_COUNT);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mFragmentId = bundle.getInt(Extras.EXTRA_FRAGMENT_ID);
            mCategoryName = bundle.getString(Extras.EXTRA_CATEGORY);
            mCategoryCount = bundle.getInt(Extras.EXTRA_COUNT);
        }

        setFragment();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleHelper.setLocale(newBase);
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCategoryName != null) {
            outState.putString(Extras.EXTRA_CATEGORY, mCategoryName);
        }
        outState.putInt(Extras.EXTRA_COUNT, mCategoryCount);
        outState.putInt(Extras.EXTRA_FRAGMENT_ID, mFragmentId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null) {
            this.setIntent(intent);
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                mFragmentId = bundle.getInt(Extras.EXTRA_FRAGMENT_ID);
                mCategoryName = bundle.getString(Extras.EXTRA_CATEGORY);
                mCategoryCount = bundle.getInt(Extras.EXTRA_COUNT);
            }

            setFragment();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        WindowHelper.resetNavigationBarTranslucent(this, WindowHelper.NavigationBarTranslucent.PORTRAIT_ONLY);
        LocaleHelper.setLocale(this);
    }

    @Override
    protected void onDestroy() {
        WindowHelper.setTranslucentStatusBar(this, true);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentId == Extras.ID_WALLPAPER_SEARCH) {
            Fragment fragment = mFragManager.findFragmentByTag(Extras.TAG_WALLPAPER_SEARCH);
            if (fragment != null) {
                WallpaperSearchFragment wallpaperSearchFragment = (WallpaperSearchFragment) fragment;
                if (!wallpaperSearchFragment.isSearchQueryEmpty()) {
                    wallpaperSearchFragment.filterSearch("");
                    return;
                }
            }

            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return;
        }

        super.onBackPressed();
    }

    private void setFragment() {
        Fragment fragment = null;
        if (mFragmentId == Extras.ID_CATEGORY_WALLPAPERS) {
            fragment = CategoryWallpapersFragment.newInstance(mCategoryName, mCategoryCount);
        } else if (mFragmentId == Extras.ID_WALLPAPER_SEARCH) {
            fragment = new WallpaperSearchFragment();
            mFragmentTag = Extras.TAG_WALLPAPER_SEARCH;
        }

        if (fragment == null) {
            finish();
            return;
        }

        FragmentTransaction ft = mFragManager.beginTransaction()
                .replace(R.id.container, fragment, mFragmentTag);
        try {
            ft.commit();
        } catch (Exception e) {
            ft.commitAllowingStateLoss();
        }
    }
}
