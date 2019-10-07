package com.sumitkolhe.bitsplash.applications;

import androidx.annotation.NonNull;

public class WallpaperBoard extends WallpaperBoardApplication {

    @NonNull
    @Override
    public WallpaperBoardConfiguration onInit() {
        WallpaperBoardConfiguration configuration = new WallpaperBoardConfiguration();
        configuration.setNavigationIcon(WallpaperBoardConfiguration.NavigationIcon.STYLE_4);
        configuration.setNavigationViewHeaderStyle(WallpaperBoardConfiguration.NavigationViewHeader.NORMAL);
        configuration.setWallpapersGridStyle(WallpaperBoardConfiguration.GridStyle.CARD);
        configuration.setDashboardThemingEnabled(true);
        return configuration;
    }
}
