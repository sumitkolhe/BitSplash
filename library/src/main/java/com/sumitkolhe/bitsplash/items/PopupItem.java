package com.sumitkolhe.bitsplash.items;

import android.content.Context;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;



public class PopupItem {

    private final String mTitle;
    private int mIcon;
    private boolean mShowCheckbox;
    private boolean mCheckboxValue;
    private boolean mIsSelected;
    private Type mType;

    public PopupItem(String title) {
        mTitle = title;
        mShowCheckbox = false;
        mCheckboxValue = false;
        mIsSelected = false;
    }

    public PopupItem setIcon(@DrawableRes int icon) {
        mIcon = icon;
        return this;
    }

    public PopupItem setShowCheckbox(boolean showCheckbox) {
        mShowCheckbox = showCheckbox;
        return this;
    }

    public PopupItem setCheckboxValue(boolean checkboxValue) {
        mCheckboxValue = checkboxValue;
        return this;
    }

    public PopupItem setSelected(boolean selected) {
        mIsSelected = selected;
        return this;
    }

    public PopupItem setType(Type type) {
        mType = type;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    @DrawableRes
    public int getIcon() {
        return mIcon;
    }

    public boolean isShowCheckbox() {
        return mShowCheckbox;
    }

    public boolean getCheckboxValue() {
        return mCheckboxValue;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public Type getType() {
        return mType;
    }

    public enum Type {
        WALLPAPER_CROP,
        HOMESCREEN,
        LOCKSCREEN,
        HOMESCREEN_LOCKSCREEN,
        DOWNLOAD,
        SORT_LATEST,
        SORT_OLDEST,
        SORT_NAME,
        SORT_RANDOM,
    }

    public static List<PopupItem> getApplyItems(@NonNull Context context) {
        List<PopupItem> items = new ArrayList<>();

        //Todo: wait until google fix the issue, then enable wallpaper crop again on API 26+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            items.add(new PopupItem(context.getResources().getString(R.string.menu_wallpaper_crop))
                    .setType(Type.WALLPAPER_CROP)
                    .setCheckboxValue(Preferences.get(context).isCropWallpaper())
                    .setShowCheckbox(true));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            items.add(new PopupItem(context.getResources().getString(R.string.menu_apply_lockscreen))
                    .setType(Type.LOCKSCREEN)
                    .setIcon(R.drawable.ic_toolbar_lockscreen));
        }

        items.add(new PopupItem(context.getResources().getString(R.string.menu_apply_homescreen))
                .setType(Type.HOMESCREEN)
                .setIcon(R.drawable.ic_toolbar_homescreen));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            items.add(new PopupItem(context.getResources().getString(R.string.menu_apply_homescreen_lockscreen))
                    .setType(Type.HOMESCREEN_LOCKSCREEN)
                    .setIcon(R.drawable.ic_toolbar_homescreen_lockscreen));
        }

        if (context.getResources().getBoolean(R.bool.enable_wallpaper_download)) {
            items.add(new PopupItem(context.getResources().getString(R.string.menu_save))
                    .setType(Type.DOWNLOAD)
                    .setIcon(R.drawable.ic_toolbar_download));
        }
        return items;
    }

    public static List<PopupItem> getSortItems(@NonNull Context context, boolean selection) {
        List<PopupItem> items = new ArrayList<>();

        items.add(new PopupItem(context.getResources().getString(R.string.menu_sort_random))
                .setType(Type.SORT_RANDOM)
                .setIcon(R.drawable.ic_toolbar_sort_random));

        items.add(new PopupItem(context.getResources().getString(R.string.menu_sort_latest))
                .setType(Type.SORT_LATEST)
                .setIcon(R.drawable.ic_toolbar_sort_latest));

        items.add(new PopupItem(context.getResources().getString(R.string.menu_sort_oldest))
                .setType(Type.SORT_OLDEST)
                .setIcon(R.drawable.ic_toolbar_sort_oldest));

       /* items.add(new PopupItem(context.getResources().getString(R.string.menu_sort_name))
                .setType(Type.SORT_NAME)
                .setIcon(R.drawable.ic_toolbar_sort_name));*/



        if (selection) {
            Type type = Preferences.get(context).getSortBy();
            int order = Preferences.get(context).getSortByOrder(type);
            if (order >= 0 && order < items.size()) {{
                items.get(order).setSelected(true);
            }}
        }
        return items;
    }
}
