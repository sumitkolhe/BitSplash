package com.sumitkolhe.bitsplash.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.TimeHelper;
import com.danimahardhika.android.helpers.core.WindowHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.activities.callbacks.MuzeiCallback;
import com.sumitkolhe.bitsplash.fragments.dialogs.FilterFragment;
import com.sumitkolhe.bitsplash.fragments.dialogs.RefreshDurationFragment;
import com.sumitkolhe.bitsplash.helpers.LocaleHelper;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.utils.listeners.RefreshDurationListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.sumitkolhe.bitsplash.helpers.ViewHelper.resetViewBottomPadding;


public abstract class WallpaperBoardMuzeiActivity extends AppCompatActivity implements MuzeiCallback,
        View.OnClickListener, RefreshDurationListener {

    @BindView(R2.id.scrollview)
    NestedScrollView mScrollView;
    @BindView(R2.id.muzei_refresh_duration)
    LinearLayout mRefreshDuration;
    @BindView(R2.id.muzei_rotate_time)
    TextView mRotateTimeText;
    @BindView(R2.id.muzei_wifi_only)
    LinearLayout mWifiOnly;
    @BindView(R2.id.muzei_wifi_only_check)
    AppCompatCheckBox mWifiOnlyCheck;
    @BindView(R2.id.muzei_select_categories)
    LinearLayout mSelectCategories;
    @BindView(R2.id.muzei_save)
    LinearLayout mSave;

    private Class<?> mMuzeiService;
    private int mRotateTime;
    private boolean mIsMinute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.setTheme(Preferences.get(this).isDarkTheme() ?
                R.style.MuzeiThemeDark : R.style.MuzeiTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muzei);
        ButterKnife.bind(this);
        mMuzeiService = onInit();

        ViewCompat.setNestedScrollingEnabled(mScrollView, false);
        WindowHelper.disableTranslucentNavigationBar(this);

        ColorHelper.setNavigationBarColor(this, ColorHelper.getDarkerColor(
                ColorHelper.getAttributeColor(this, R.attr.colorAccent), 0.8f));
        ColorHelper.setStatusBarColor(this, ColorHelper.getAttributeColor(
                this, R.attr.colorPrimaryDark));
        ColorHelper.setupStatusBarIconColor(this);

        int color = ColorHelper.getAttributeColor(this, R.attr.toolbar_icon);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(DrawableHelper.getTintedDrawable(
                this, R.drawable.ic_toolbar_muzei, color));
        setSupportActionBar(toolbar);

        mIsMinute = Preferences.get(this).isRotateMinute();
        mRotateTime = TimeHelper.milliToMinute(
                Preferences.get(this).getRotateTime());
        if (!mIsMinute) mRotateTime = mRotateTime / 60;

        initRefreshDuration();
        initSettings();

        mWifiOnly.setOnClickListener(this);
        mSelectCategories.setOnClickListener(this);
        mRefreshDuration.setOnClickListener(this);
        mSave.setOnClickListener(this);

        mWifiOnlyCheck.setChecked(Preferences.get(this).isWifiOnly());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleHelper.setLocale(newBase);
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetViewBottomPadding(mScrollView, false);
        LocaleHelper.setLocale(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.muzei_refresh_duration) {
            RefreshDurationFragment.showRefreshDurationDialog(getSupportFragmentManager(), mRotateTime, mIsMinute);
        } else if (id == R.id.muzei_wifi_only) {
            mWifiOnlyCheck.setChecked(!mWifiOnlyCheck.isChecked());
        } else if (id == R.id.muzei_select_categories) {
            FilterFragment.showFilterDialog(getSupportFragmentManager(), true);
        } else if (id == R.id.muzei_save) {
            int rotateTime = TimeHelper.minuteToMilli(mRotateTime);
            if (!mIsMinute) rotateTime = rotateTime * 60;

            Preferences.get(this).setRotateMinute(mIsMinute);
            Preferences.get(this).setRotateTime(rotateTime);
            Preferences.get(this).setWifiOnly(mWifiOnlyCheck.isChecked());

            Intent intent = new Intent(this, mMuzeiService);
            intent.putExtra("restart", true);
            startService(intent);

            Toast.makeText(this, R.string.muzei_settings_saved,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onRefreshDurationSet(int rotateTime, boolean isMinute) {
        mRotateTime = rotateTime;
        mIsMinute = isMinute;
        initRefreshDuration();
    }

    private void initRefreshDuration() {
        String refreshDuration = getResources().getString(R.string.muzei_refresh_duration_desc) +" "+
                mRotateTime +" "+ getResources().getString(mIsMinute ? R.string.minute : R.string.hour);
        mRotateTimeText.setText(refreshDuration);
    }

    private void initSettings() {
        int color = ColorHelper.getAttributeColor(this, R.attr.colorAccent);
        int titleColor = ColorHelper.getTitleTextColor(color);

        ImageView icon = findViewById(R.id.muzei_save_icon);
        icon.setImageDrawable(DrawableHelper.getTintedDrawable(this, R.drawable.ic_toolbar_save, titleColor));
        TextView text = findViewById(R.id.muzei_save_text);
        text.setTextColor(titleColor);
    }
}
