package com.sumitkolhe.bitsplash.helpers;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;



public class TypefaceHelper {

    private static HashMap<String, WeakReference<Typeface>> mTypefaces = new HashMap<>();
    private static final String REGULAR = "regular";
    private static final String MEDIUM = "medium";
    private static final String BOLD = "bold";
    private static final String LOGO = "logo";

    @Nullable
    public static Typeface getRegular(@NonNull Context context) {
        try {
            if (!mTypefaces.containsKey(REGULAR) || mTypefaces.get(REGULAR).get() == null) {
                Typeface typeface = Typeface.createFromAsset(
                        context.getAssets(), "fonts/Font-Regular.ttf");
                mTypefaces.put(REGULAR, new WeakReference<>(typeface));
            }
            return mTypefaces.get(REGULAR).get();
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static Typeface getMedium(@NonNull Context context) {
        try {
            if (!mTypefaces.containsKey(MEDIUM) || mTypefaces.get(MEDIUM).get() == null) {
                Typeface typeface = Typeface.createFromAsset(
                        context.getAssets(), "fonts/Font-Medium.ttf");
                mTypefaces.put(MEDIUM, new WeakReference<>(typeface));
            }
            return mTypefaces.get(MEDIUM).get();
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static Typeface getBold(@NonNull Context context) {
        try {
            if (!mTypefaces.containsKey(BOLD) || mTypefaces.get(BOLD).get() == null) {
                Typeface typeface = Typeface.createFromAsset(
                        context.getAssets(), "fonts/Font-Bold.ttf");
                mTypefaces.put(BOLD, new WeakReference<>(typeface));
            }
            return mTypefaces.get(BOLD).get();
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static Typeface getLogo(@NonNull Context context) {
        try {
            if (!mTypefaces.containsKey(LOGO) || mTypefaces.get(LOGO).get() == null) {
                Typeface typeface = Typeface.createFromAsset(
                        context.getAssets(), "fonts/Font-Logo.ttf");
                mTypefaces.put(LOGO, new WeakReference<>(typeface));
            }
            return mTypefaces.get(LOGO).get();
        } catch (Exception e) {
            return null;
        }
    }
}
