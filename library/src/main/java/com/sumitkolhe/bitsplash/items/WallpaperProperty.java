package com.sumitkolhe.bitsplash.items;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.helpers.WallpaperHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class WallpaperProperty {

    private int mIcon;
    private String mTitle;
    private String mDesc;

    public int getIcon() {
        return mIcon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDesc() {
        return mDesc;
    }

    public WallpaperProperty setIcon(@DrawableRes int icon) {
        mIcon = icon;
        return this;
    }

    public WallpaperProperty setTitle(String title) {
        mTitle = title;
        return this;
    }

    public WallpaperProperty setDesc(String desc) {
        mDesc = desc;
        return this;
    }

    public static List<WallpaperProperty> getWallpaperProperties(@NonNull Context context, @NonNull Wallpaper wallpaper) {
        List<WallpaperProperty> properties = new ArrayList<>();

        properties.add(new WallpaperProperty()
                .setIcon(R.drawable.ic_toolbar_details_name)
                .setTitle(wallpaper.getName())
                .setDesc(context.getResources().getString(
                        R.string.wallpaper_property_name, wallpaper.getName())));

        properties.add(new WallpaperProperty()
                .setIcon(R.drawable.ic_toolbar_details_author)
                .setTitle(wallpaper.getAuthor())
                .setDesc(context.getResources().getString(
                        R.string.wallpaper_property_author, wallpaper.getAuthor())));

        if (wallpaper.getDimensions() != null) {
            String title = wallpaper.getDimensions().getWidth() +" x "+ wallpaper.getDimensions().getHeight() +" pixels";
            properties.add(new WallpaperProperty()
                    .setIcon(R.drawable.ic_toolbar_details_dimensions)
                    .setTitle(title)
                    .setDesc(context.getResources().getString(
                            R.string.wallpaper_property_dimensions, title)));
        }

        if (wallpaper.getMimeType() != null) {
            String title = WallpaperHelper.getFormat(wallpaper.getMimeType()).toUpperCase(Locale.getDefault());
            properties.add(new WallpaperProperty()
                    .setIcon(R.drawable.ic_toolbar_details_format)
                    .setTitle(title)
                    .setDesc(context.getResources().getString(
                            R.string.wallpaper_property_format, title)));
        }

        if (wallpaper.getSize() != 0) {
            String title = WallpaperHelper.getSize(wallpaper.getSize());
            properties.add(new WallpaperProperty()
                    .setIcon(R.drawable.ic_toolbar_details_size)
                    .setTitle(title)
                    .setDesc(context.getResources().getString(
                            R.string.wallpaper_property_size, title)));
        }
        return properties;
    }
}
