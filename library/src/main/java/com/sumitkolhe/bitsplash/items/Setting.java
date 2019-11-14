package com.sumitkolhe.bitsplash.items;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;


public class Setting {

    private final int mIcon;
    private final String mTitle;
    private final String mSubtitle;
    private String mContent;
    private String mFooter;
    private final Type mType;
    private final int mCheckState;

    private Setting(@DrawableRes int icon, String title, String subtitle, String content, String footer,
                   @NonNull Type type, @IntRange(from = -1, to = 1) int checkState) {
        mIcon = icon;
        mTitle = title;
        mSubtitle = subtitle;
        mContent = content;
        mFooter = footer;
        mType = type;
        mCheckState = checkState;
    }

    @DrawableRes
    public int getIcon() {
        return mIcon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public String getContent() {
        return mContent;
    }

    public String getFooter() {
        return mFooter;
    }

    public Setting.Type getType() {
        return mType;
    }

    public int getCheckState() {
        return mCheckState;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setFooter(String footer) {
        mFooter = footer;
    }

    public static Builder Builder(@NonNull Type type) {
        return new Builder(type);
    }

    public static class Builder {

        private int mIcon;
        private String mTitle;
        private String mSubtitle;
        private String mContent;
        private String mFooter;
        private Type mType;
        private int mCheckState;

        private Builder(@NonNull Type type) {
            mIcon = -1;
            mTitle = "";
            mSubtitle = "";
            mContent = "";
            mFooter = "";
            mCheckState = -1;
            mType = type;
        }

        public Builder icon(@DrawableRes int icon) {
            mIcon = icon;
            return this;
        }

        public Builder title(String title) {
            mTitle = title;
            return this;
        }

        public Builder subtitle(String subtitle) {
            mSubtitle = subtitle;
            return this;
        }

        public Builder content(String content) {
            mContent = content;
            return this;
        }

        public  Builder footer(String footer) {
            mFooter = footer;
            return this;
        }

        public Builder checkboxState(@IntRange(from = -1, to = 1) int checkboxState) {
            mCheckState = checkboxState;
            return this;
        }

        public Setting build() {
            return new Setting(mIcon, mTitle, mSubtitle, mContent, mFooter, mType, mCheckState);
        }
    }

    public enum Type {
        HEADER,
        CACHE,
        THEME,
        WALLPAPER,
        PREVIEW_QUALITY,
        LANGUAGE,
        RESET_TUTORIAL,
        APP_VERSION;
    }
}
