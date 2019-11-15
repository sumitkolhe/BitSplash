package com.sumitkolhe.bitsplash.activities.configurations;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.sumitkolhe.bitsplash.helpers.TypefaceHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class SplashScreenConfiguration {

    @NonNull private Class<?> mMainActivity;
    @ColorInt private int mBottomTextColor;
    private String mBottomText;
    @FontSize private int mBottomTextSize;
    @FontStyle private int mBottomTextFont;

    public SplashScreenConfiguration(@NonNull Class<?> mainActivity) {
        mMainActivity = mainActivity;
        mBottomTextColor = -1;
        mBottomTextFont = FontStyle.LOGO;
        mBottomTextSize = FontSize.LARGE;
    }

    public SplashScreenConfiguration setBottomText(String text) {
        mBottomText = text;
        return this;
    }

    public SplashScreenConfiguration setBottomTextColor(@ColorInt int color) {
        mBottomTextColor = color;
        return this;
    }

    public SplashScreenConfiguration setBottomTextSize(@FontSize int fontSize) {
        mBottomTextSize = fontSize;
        return this;
    }

    public SplashScreenConfiguration setBottomTextFont(@FontStyle int fontStyle) {
        mBottomTextFont = fontStyle;
        return this;
    }

    public Class<?> getMainActivity() {
        return mMainActivity;
    }

    public String getBottomText() {
        return mBottomText;
    }

    public int getBottomTextColor() {
        return mBottomTextColor;
    }

    public float getBottomTextSize() {
        switch (mBottomTextSize) {
            case FontSize.SMALL:
                return 14f;
            case FontSize.LARGE:
                return 24f;
            case FontSize.REGULAR:
            default:
                return 15f;
        }
    }

    public Typeface getBottomTextFont(@NonNull Context context) {
        switch (mBottomTextFont) {
            case FontStyle.MEDIUM:
                return TypefaceHelper.getMedium(context);
            case FontStyle.BOLD:
                return TypefaceHelper.getBold(context);
            case FontStyle.REGULAR:
                return TypefaceHelper.getRegular(context);
            case FontStyle.LOGO:

            default:
                return TypefaceHelper.getLogo(context);

        }
    }

    @IntDef({FontSize.SMALL, FontSize.REGULAR, FontSize.LARGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FontSize {
        int SMALL = 0;
        int REGULAR = 1;
        int LARGE = 2;
    }

    @IntDef({FontStyle.REGULAR, FontStyle.MEDIUM, FontStyle.BOLD, FontStyle.LOGO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FontStyle {
        int REGULAR = 0;
        int MEDIUM = 1;
        int BOLD = 2;
        int LOGO = 3;
    }
}
