package com.sumitkolhe.bitsplash.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danimahardhika.android.helpers.core.FileHelper;
import com.danimahardhika.android.helpers.core.ViewHelper;
import com.sumitkolhe.bitsplash.board.BuildConfig;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.adapters.SettingsAdapter;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.helpers.ConfigurationHelper;
import com.sumitkolhe.bitsplash.helpers.WallpaperHelper;
import com.sumitkolhe.bitsplash.items.Setting;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.sumitkolhe.bitsplash.utils.listeners.NavigationListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sumitkolhe.bitsplash.helpers.ViewHelper.resetViewBottomPadding;

public class SettingsFragment extends Fragment {

    @BindView(R2.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R2.id.toolbar)
    Toolbar mToolbar;
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        if (!Preferences.get(getActivity()).isShadowEnabled()) {
            View shadow = view.findViewById( R.id.shadow);
            if (shadow != null) shadow.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resetViewBottomPadding(mRecyclerView, true);
        ViewHelper.setupToolbar(mToolbar);

        TextView textView = getActivity().findViewById(R.id.title);
        textView.setText(getActivity().getResources().getString(
                R.string.navigation_view_settings));

        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(ConfigurationHelper.getNavigationIcon(getActivity(),
                WallpaperBoardApplication.getConfig().getNavigationIcon()));
        mToolbar.setNavigationOnClickListener(view -> {
            try {
                NavigationListener listener = (NavigationListener) getActivity();
                listener.onNavigationIconClick();
            } catch (IllegalStateException e) {
                LogUtil.e("Parent activity must implements NavigationListener");
            }
        });

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initSettings();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetViewBottomPadding(mRecyclerView, true);
    }

    private void initSettings() {
        List<Setting> settings = new ArrayList<>();

        double cache = (double) FileHelper.getDirectorySize(getActivity().getCacheDir()) / FileHelper.MB;
        NumberFormat formatter = new DecimalFormat("#0.00");

        settings.add(Setting.Builder(Setting.Type.HEADER)
                .icon(R.drawable.ic_toolbar_storage)
                .title(getActivity().getResources().getString(R.string.pref_data_header))
                .build()
        );

        settings.add(Setting.Builder(Setting.Type.CACHE)
                .subtitle(getActivity().getResources().getString(R.string.pref_data_cache))
                .content(getActivity().getResources().getString(R.string.pref_data_cache_desc))
                .footer(String.format(getActivity().getResources().getString(R.string.pref_data_cache_size),
                        formatter.format(cache) + " MB"))
                .build()
        );

        settings.add(Setting.Builder(Setting.Type.HEADER)
                .icon(R.drawable.ic_toolbar_theme)
                .title(getActivity().getResources().getString(R.string.pref_theme_header))
                .build()
        );

        settings.add(Setting.Builder(Setting.Type.THEME)
                .subtitle(getActivity().getResources().getString(R.string.pref_theme_dark))
                .content(getActivity().getResources().getString(R.string.pref_theme_dark_desc))
                .checkboxState(Preferences.get(getActivity()).isDarkTheme() ? 1 : 0)
                .build()
        );

        settings.add(Setting.Builder(Setting.Type.HEADER)
                .icon(R.drawable.ic_toolbar_wallpapers)
                .title(getActivity().getResources().getString(R.string.pref_wallpaper_header))
                .build()
        );

        settings.add(Setting.Builder(Setting.Type.PREVIEW_QUALITY)
                .subtitle(getActivity().getResources().getString(R.string.pref_wallpaper_high_quality_preview))
                .content(Preferences.get(getActivity()).isHighQualityPreviewEnabled() ?
                        getActivity().getResources().getString(R.string.pref_wallpaper_high_quality_preview_high) :
                        getActivity().getResources().getString(R.string.pref_wallpaper_high_quality_preview_low))
                .build()
        );

        settings.add(Setting.Builder(Setting.Type.WALLPAPER)
                .subtitle(getActivity().getResources().getString(R.string.pref_wallpaper_location))
                .content(WallpaperHelper.getDefaultWallpapersDirectory(getActivity()).toString())
                .build()
        );

       /* settings.add(Setting.Builder(Setting.Type.HEADER)
                .icon(R.drawable.ic_toolbar_language)
                .title(getActivity().getResources().getString(R.string.pref_language_header))
                .build()
        );

        Language language = LocaleHelper.getCurrentLanguage(getActivity());
        settings.add(Setting.Builder(Setting.Type.LANGUAGE)
                .subtitle(Preferences.get(getActivity()).isLocaleDefault() ?
                        getString(R.string.pref_options_default) : language.getName())
                .build()
        ); */

        settings.add(Setting.Builder(Setting.Type.HEADER)
                .icon(R.drawable.ic_toolbar_others)
                .title(getActivity().getResources().getString(R.string.pref_others_header))
                .build()
        );

        settings.add(Setting.Builder(Setting.Type.RESET_TUTORIAL)
                .subtitle(getActivity().getResources().getString(R.string.pref_others_reset_tutorial))
                .build()
        );

        settings.add(Setting.Builder(Setting.Type.HEADER)
                .icon(R.drawable.ic_toolbar_dashboard)
                .title("BitSplash Version")
                .build()
        );
        settings.add(Setting.Builder(Setting.Type.HEADER)
                .subtitle("v"+versionName)
                .build()
        );

        mRecyclerView.setAdapter(new SettingsAdapter(getActivity(), settings));
    }
}
