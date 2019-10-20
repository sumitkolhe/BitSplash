package com.sumitkolhe.bitsplash.items;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;



public class ColorPalette {

    private List<Integer> mColors;

    public ColorPalette() {
        mColors = new ArrayList<>();
    }

    public ColorPalette add(@ColorInt int color) {
        if (color == 0) {
            LogUtil.e("color: " +color+ " isn't valid, color ignored");
            return this;
        }
        if (!mColors.contains(color)) {
            mColors.add(color);
            return this;
        }
        LogUtil.e("ColorPalette: color already added");
        return this;
    }

    @ColorInt
    public int get(int index) {
        if (index >= 0 && index < mColors.size()) {
            return mColors.get(index);
        }
        LogUtil.e("ColorPalette: index out of bounds");
        return -1;
    }

    @Nullable
    public String getHex(int index) {
        if (index >= 0 && index < mColors.size()) {
            return String.format("#%06X", (0xFFFFFF & get(index)));
        }
        LogUtil.e("ColorPalette: index out of bounds");
        return null;
    }

    public int size() {
        return mColors.size();
    }
}
