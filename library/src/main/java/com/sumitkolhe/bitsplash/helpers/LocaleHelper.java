package com.sumitkolhe.bitsplash.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import androidx.annotation.NonNull;

import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.items.Language;
import com.sumitkolhe.bitsplash.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class LocaleHelper {

    public static void setLocale(@NonNull Context context) {
        Locale locale = Preferences.get(context).getCurrentLocale();
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.setDefault(new LocaleList(locale));
            configuration.setLocales(new LocaleList(locale));
            configuration.setLocale(locale);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        //Todo:
        // Find out a solution to use context.createConfigurationContext(configuration);
        // It breaks onConfigurationChanged()
        // Still can't find a way to fix that
        // No other options, better use deprecated code for now
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    public static Locale getSystem() {
        Locale locale = Resources.getSystem().getConfiguration().locale;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        }
        return locale;
    }

    public static List<Language> getAvailableLanguages(@NonNull Context context) {
        List<Language> languages = new ArrayList<>();
        String[] names = context.getResources().getStringArray(R.array.languages_name);
        String[] codes = context.getResources().getStringArray(R.array.languages_code);

        for (int i = 0; i < names.length; i++) {
            Language language = new Language(names[i], getLocale(codes[i]));
            languages.add(language);
        }
        return languages;
    }

    public static Language getCurrentLanguage(@NonNull Context context) {
        List<Language> languages = getAvailableLanguages(context);
        Locale locale = Preferences.get(context).getCurrentLocale();

        for (Language language : languages) {
            Locale l = language.getLocale();
            if (locale.toString().equals(l.toString())) {
                return language;
            }
        }
        return new Language("English", new Locale("en", "US"));
    }

    public static Locale getLocale(String language) {
        String[] codes = language.split("_");
        if (codes.length == 2) {
            return new Locale(codes[0], codes[1]);
        }
        return Locale.getDefault();
    }
}
