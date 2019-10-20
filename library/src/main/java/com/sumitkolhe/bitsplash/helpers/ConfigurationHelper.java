package com.sumitkolhe.bitsplash.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardConfiguration;

import static com.danimahardhika.android.helpers.core.DrawableHelper.getTintedDrawable;


public abstract class ConfigurationHelper {

    @Nullable
    public static Drawable getNavigationIcon(@NonNull Context context, @WallpaperBoardConfiguration.NavigationIcon int navigationIcon) {
        int color = ColorHelper.getAttributeColor(context, R.attr.toolbar_icon);
        switch (navigationIcon) {
            case WallpaperBoardConfiguration.NavigationIcon.STYLE_1:
                return getTintedDrawable(context, R.drawable.ic_toolbar_navigation_1, color);
            case WallpaperBoardConfiguration.NavigationIcon.STYLE_2:
                return getTintedDrawable(context, R.drawable.ic_toolbar_navigation_2, color);
            case WallpaperBoardConfiguration.NavigationIcon.STYLE_3:
                return getTintedDrawable(context, R.drawable.ic_toolbar_navigation_3, color);
            case WallpaperBoardConfiguration.NavigationIcon.STYLE_4:
                return getTintedDrawable(context, R.drawable.ic_toolbar_navigation_4, color);
            case WallpaperBoardConfiguration.NavigationIcon.DEFAULT:
            default:
                return getTintedDrawable(context, R.drawable.ic_toolbar_navigation, color);
        }
    }
}
