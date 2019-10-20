package com.sumitkolhe.bitsplash.items;

import java.util.Locale;


public class Language {

    private String mName;
    private Locale mLocale;

    public Language(String name, Locale locale) {
        mName = name;
        mLocale = locale;
    }

    public String getName() {
        return mName;
    }

    public Locale getLocale() {
        return mLocale;
    }
}
