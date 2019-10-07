package com.sumitkolhe.bitsplash.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.SoftKeyboardHelper;
import com.danimahardhika.android.helpers.core.ViewHelper;
import com.danimahardhika.android.helpers.core.WindowHelper;
import com.danimahardhika.android.helpers.license.LicenseHelper;
import com.danimahardhika.android.helpers.permission.PermissionCode;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.activities.callbacks.ActivityCallback;
import com.sumitkolhe.bitsplash.activities.configurations.ActivityConfiguration;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardConfiguration;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.fragments.AboutFragment;
import com.sumitkolhe.bitsplash.fragments.CollectionFragment;
import com.sumitkolhe.bitsplash.fragments.FavoritesFragment;
import com.sumitkolhe.bitsplash.fragments.SettingsFragment;
import com.sumitkolhe.bitsplash.fragments.dialogs.InAppBillingFragment;
import com.sumitkolhe.bitsplash.helpers.BackupHelper;
import com.sumitkolhe.bitsplash.helpers.LicenseCallbackHelper;
import com.sumitkolhe.bitsplash.helpers.LocaleHelper;
import com.sumitkolhe.bitsplash.items.InAppBilling;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.services.WallpaperBoardService;
import com.sumitkolhe.bitsplash.tasks.LocalFavoritesBackupTask;
import com.sumitkolhe.bitsplash.tasks.LocalFavoritesRestoreTask;
import com.sumitkolhe.bitsplash.tasks.WallpapersLoaderTask;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.sumitkolhe.bitsplash.utils.ImageConfig;
import com.sumitkolhe.bitsplash.utils.InAppBillingProcessor;
import com.sumitkolhe.bitsplash.utils.listeners.InAppBillingListener;
import com.sumitkolhe.bitsplash.utils.listeners.NavigationListener;
import com.sumitkolhe.bitsplash.utils.views.HeaderView;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/*
 * Wallpaper Board
 *
 * Copyright (c) 2017 Dani Mahardhika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public abstract class WallpaperBoardActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        InAppBillingListener, NavigationListener, ActivityCallback {

    @BindView(R2.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R2.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager mFragManager;
    private LicenseHelper mLicenseHelper;

    private String mFragmentTag;
    private int mPosition, mLastPosition;

    private ActivityConfiguration mConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.setTheme(Preferences.get(this).isDarkTheme() ?
                R.style.AppThemeDark : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_board);


        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.start();

        new AppUpdater(this)
                .showEvery(5)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY);

        ButterKnife.bind(this);
        startService(new Intent(this, WallpaperBoardService.class));

        //Todo: wait until google fix the issue, then enable wallpaper crop again on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Preferences.get(this).setCropWallpaper(false);
        }

        mConfig = onInit();
        InAppBillingProcessor.get(this).init(mConfig.getLicenseKey());

        WindowHelper.resetNavigationBarTranslucent(this,
                WindowHelper.NavigationBarTranslucent.PORTRAIT_ONLY);

        SoftKeyboardHelper softKeyboardHelper = new SoftKeyboardHelper(this,
                findViewById(R.id.container));
        softKeyboardHelper.enable();

        mFragManager = getSupportFragmentManager();

        initNavigationView();
        initNavigationViewHeader();

        mPosition = mLastPosition = 0;
        if (savedInstanceState != null) {
            mPosition = mLastPosition = savedInstanceState.getInt(Extras.EXTRA_POSITION, 0);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int position = bundle.getInt(Extras.EXTRA_POSITION, -1);
            if (position >= 0 && position < 5) {
                mPosition = mLastPosition = position;
            }
        }

        setFragment(getFragment(mPosition));
        if (!WallpaperBoardApplication.isLatestWallpapersLoaded()) {
            WallpapersLoaderTask.with(this)
                    .callback(success -> {
                        if (!success) return;

                        Fragment fragment = mFragManager.findFragmentByTag(Extras.TAG_COLLECTION);
                        if (fragment != null && fragment instanceof CollectionFragment) {
                            ((CollectionFragment) fragment).refreshCategories();
                        }
                    })
                    .start();
        }

        if (Preferences.get(this).isFirstRun()) {
            File file = new File(BackupHelper.getDefaultDirectory(this), BackupHelper.FILE_BACKUP);
            Preferences.get(this).setPreviousBackupExist(file.exists());
        }

        if (Preferences.get(this).isFirstRun() && mConfig.isLicenseCheckerEnabled()) {
            mLicenseHelper = new LicenseHelper(this);
            mLicenseHelper.run(mConfig.getLicenseKey(),
                    mConfig.getRandomString(),
                    new LicenseCallbackHelper(this));
            return;
        }

        if (!mConfig.isLicenseCheckerEnabled()) {
            Preferences.get(this).setFirstRun(false);
        }

        if (mConfig.isLicenseCheckerEnabled() && !Preferences.get(this).isLicensed()) {
            finish();
        }

         OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .unsubscribeWhenNotificationsAreDisabled(true)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.sendTag(getString(R.string.app_name), getString(R.string.app_version));

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        status.getPermissionStatus().getEnabled();

        status.getSubscriptionStatus().getSubscribed();
        status.getSubscriptionStatus().getUserSubscriptionSetting();
        status.getSubscriptionStatus().getUserId();
        status.getSubscriptionStatus().getPushToken();

        OneSignal.clearOneSignalNotifications();

    }

    class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String customKey;

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }
        }
    }

    class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String customKey;

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleHelper.setLocale(newBase);
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extras.EXTRA_POSITION, mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetNavigationView(newConfig.orientation);
        WindowHelper.resetNavigationBarTranslucent(this,
                WindowHelper.NavigationBarTranslucent.PORTRAIT_ONLY);
        LocaleHelper.setLocale(this);
    }

    @Override
    protected void onDestroy() {
        InAppBillingProcessor.get(this).destroy();

        if (mLicenseHelper != null) {
            mLicenseHelper.destroy();
        }

        stopService(new Intent(this, WallpaperBoardService.class));
        Database.get(this.getApplicationContext()).closeDatabase();

        if (!Preferences.get(this).isPreviousBackupExist()) {
            LocalFavoritesBackupTask.with(this.getApplicationContext())
                    .start(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mFragManager.getBackStackEntryCount() > 0) {
            clearBackStack();
            return;
        }

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        if (!mFragmentTag.equals(Extras.TAG_COLLECTION)) {
            mPosition = mLastPosition = 0;
            setFragment(getFragment(mPosition));
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!InAppBillingProcessor.get(this).handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionCode.STORAGE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_storage_denied, Toast.LENGTH_LONG).show();
                return;
            }

            if (!Preferences.get(this).isBackupRestored()) {
                LocalFavoritesRestoreTask.with(this).start(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @Override
    public void onNavigationIconClick() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onInAppBillingSelected(InAppBilling product) {
        InAppBillingProcessor.get(this).getProcessor()
                .purchase(this, product.getProductId());
    }

    @Override
    public void onInAppBillingConsume(String productId) {
        if (InAppBillingProcessor.get(this).getProcessor().consumePurchase(productId)) {
            new MaterialDialog.Builder(this)
                    .title(R.string.navigation_view_donate)
                    .content(R.string.donation_success)
                    .positiveText(R.string.close)
                    .show();
        }
    }

    private void initNavigationView() {
        resetNavigationView(getResources().getConfiguration().orientation);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, null, R.string.txt_open, R.string.txt_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ColorHelper.setupStatusBarIconColor(WallpaperBoardActivity.this, false);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                int color = ColorHelper.getAttributeColor(WallpaperBoardActivity.this, R.attr.colorPrimary);
                ColorHelper.setupStatusBarIconColor(WallpaperBoardActivity.this, ColorHelper.isLightColor(color));

                if (mPosition == 4) {
                    mPosition = mLastPosition;
                    mNavigationView.getMenu().getItem(mPosition).setChecked(true);
                    InAppBillingFragment.showInAppBillingDialog(mFragManager,
                            mConfig.getLicenseKey(),
                            mConfig.getDonationProductsId());
                    return;
                }

                if (mPosition != mLastPosition) {
                    mLastPosition = mPosition;
                    setFragment(getFragment(mPosition));
                }
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(false);

        mDrawerLayout.setDrawerShadow(R.drawable.navigation_view_shadow, GravityCompat.START);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        ColorStateList colorStateList = ContextCompat.getColorStateList(this,
                Preferences.get(this).isDarkTheme() ?
                        R.color.navigation_view_item_highlight_dark :
                        R.color.navigation_view_item_highlight);

        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.navigation_view_donate);
        if (menuItem != null) {
            menuItem.setVisible(getResources().getBoolean(R.bool.enable_donation));
        }

        mNavigationView.setItemTextColor(colorStateList);
        mNavigationView.setItemIconTintList(colorStateList);
        Drawable background = ContextCompat.getDrawable(this,
                Preferences.get(this).isDarkTheme() ?
                        R.drawable.navigation_view_item_background_dark :
                        R.drawable.navigation_view_item_background);
        mNavigationView.setItemBackground(background);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_view_wallpapers) mPosition = 0;
            else if (id == R.id.navigation_view_favorites) mPosition = 1;
            else if (id == R.id.navigation_view_settings) mPosition = 2;
            else if (id == R.id.navigation_view_about) mPosition = 3;
            else if (id == R.id.navigation_view_donate) mPosition = 4;
            else if (id == R.id.navigation_view_share) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_app_subject,
                        getResources().getString(R.string.app_name)));
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_app_body,
                        getResources().getString(R.string.app_name),
                        "https://play.google.com/store/apps/details?id=" +getPackageName()));
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.email_client)));
                return false;

            } else if (id == R.id.navigation_view_rate) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://play.google.com/store/apps/details?id=" +getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
                return false;
            }

            item.setChecked(true);
            mDrawerLayout.closeDrawers();
            return true;
        });
        ViewHelper.hideNavigationViewScrollBar(mNavigationView);
    }

    private void initNavigationViewHeader() {
        if (WallpaperBoardApplication.getConfig().getNavigationViewHeader() ==
                WallpaperBoardConfiguration.NavigationViewHeader.NONE) {
            mNavigationView.removeHeaderView(mNavigationView.getHeaderView(0));
            return;
        }

        String imageUrl = getResources().getString(R.string.navigation_view_header);
        String titleText = getResources().getString(R.string.navigation_view_header_title);
        View header = mNavigationView.getHeaderView(0);

        HeaderView image = header.findViewById(R.id.header_image);
        LinearLayout container = header.findViewById(R.id.header_title_container);
        TextView title = header.findViewById(R.id.header_title);
        TextView version = header.findViewById(R.id.header_version);

        if (WallpaperBoardApplication.getConfig().getNavigationViewHeader() ==
                WallpaperBoardConfiguration.NavigationViewHeader.MINI) {
            image.setRatio(16, 9);
        }

        if (titleText.length() == 0) {
            container.setVisibility(View.GONE);
        } else {
            title.setText(titleText);
            try {
                String versionText = "v" + getPackageManager()
                        .getPackageInfo(getPackageName(), 0).versionName;
                version.setText(versionText);
            } catch (Exception ignored) {}
        }

        if (ColorHelper.isValidColor(imageUrl)) {
            image.setBackgroundColor(Color.parseColor(imageUrl));
            return;
        }

        if (!URLUtil.isValidUrl(imageUrl)) {
            imageUrl = "drawable://" + DrawableHelper.getResourceId(this, imageUrl);
        }

        ImageLoader.getInstance().displayImage(imageUrl, new ImageViewAware(image),
                ImageConfig.getDefaultImageOptions(), new ImageSize(720, 720), null, null);
    }

    private void resetNavigationView(int orientation) {
        int index = mNavigationView.getMenu().size() - 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                mNavigationView.getMenu().getItem(index).setVisible(true);
                mNavigationView.getMenu().getItem(index).setEnabled(false);
                return;
            }
        }
        mNavigationView.getMenu().getItem(index).setVisible(false);
    }

    public void openDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void setFragment(Fragment fragment) {
        if (fragment == null) return;
        clearBackStack();

        FragmentTransaction ft = mFragManager.beginTransaction().replace(
                R.id.container, fragment, mFragmentTag);
        try {
            ft.commit();
        } catch (Exception e) {
            ft.commitAllowingStateLoss();
        }

        mNavigationView.getMenu().getItem(mPosition).setChecked(true);
    }


    @Nullable
    private Fragment getFragment(int position) {
        if (position == 0) {
            mFragmentTag = Extras.TAG_COLLECTION;
            return new CollectionFragment();
        } else if (position == 1) {
            mFragmentTag = Extras.TAG_FAVORITES;
            return new FavoritesFragment();
        } else if (position == 2) {
            mFragmentTag = Extras.TAG_SETTINGS;
            return new SettingsFragment();
        } else if (position == 3) {
            mFragmentTag = Extras.TAG_ABOUT;
            return new AboutFragment();
        }
        return null;
    }

    private void clearBackStack() {
        if (mFragManager.getBackStackEntryCount() > 0) {
            mFragManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
