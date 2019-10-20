package com.sumitkolhe.bitsplash.items;



public class Category {

    private int mId;
    private String mName;
    private String mThumbUrl;
    private boolean mIsSelected;
    private boolean mIsMuzeiSelected;
    private int mCount;
    private int mColor;

    private Category(String name) {
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getThumbUrl() {
        return mThumbUrl;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public boolean isMuzeiSelected() {
        return mIsMuzeiSelected;
    }

    public int getCount() {
        return mCount;
    }

    public int getColor() {
        return mColor;
    }

    public String getCategoryCount() {
        if (mCount > 100 && mCount < 1000) {
            return "99+";
        } else if (mCount > 1000 && mCount < 10000) {
            String string = String.valueOf(mCount);
            int lastIndex = string.length() - 3;
            if (lastIndex >= 0 && lastIndex < string.length()) {
                return string.substring(0, lastIndex) + "K+";
            }
        } else if (mCount > 10000) {
            return "9K+";
        }
        return String.valueOf(mCount);
    }

    public void setId(int id) {
        mId = id;
    }

    public void setThumbUrl(String thumbUrl) {
        mThumbUrl = thumbUrl;
    }

    public void setSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    public void setMuzeiSelected(boolean isMuzeiSelected) {
        mIsMuzeiSelected = isMuzeiSelected;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public boolean equals(Object object) {
        boolean equals = false;
        if (object != null && object instanceof Category) {
            equals = mName.equals(((Category) object).getName());
        }
        return equals;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {

        private int mId;
        private String mName;
        private String mThumbUrl;
        private boolean mIsSelected;
        private boolean mIsMuzeiSelected;
        private int mCount;
        private int mColor;

        private Builder() {
            mId = -1;
            mCount = 0;
            mIsMuzeiSelected = false;
            mIsSelected = false;
        }

        public Builder id(int id) {
            mId = id;
            return this;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder thumbUrl(String thumbUrl) {
            mThumbUrl = thumbUrl;
            return this;
        }

        public Builder selected(boolean isSelected) {
            mIsSelected = isSelected;
            return this;
        }

        public Builder muzeiSelected(boolean isMuzeiSelected) {
            mIsMuzeiSelected = isMuzeiSelected;
            return this;
        }

        public Builder count(int count) {
            mCount = count;
            return this;
        }

        public Builder color(int color) {
            mColor = color;
            return this;
        }

        public Category build() {
            Category category = new Category(mName);
            category.setId(mId);
            category.setThumbUrl(mThumbUrl);
            category.setSelected(mIsSelected);
            category.setMuzeiSelected(mIsMuzeiSelected);
            category.setCount(mCount);
            category.setColor(mColor);
            return category;
        }
    }
}
