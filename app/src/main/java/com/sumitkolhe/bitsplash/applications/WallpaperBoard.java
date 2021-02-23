package com.sumitkolhe.bitsplash.applications;

import androidx.annotation.NonNull;

public class WallpaperBoard extends WallpaperBoardApplication {

    //to enable author name and wallpaper name remove comments from jsonstructure.java and jsonhelper.java

    @NonNull
    @Override
    public WallpaperBoardConfiguration onInit() {
        WallpaperBoardConfiguration configuration = new WallpaperBoardConfiguration();
        configuration.setNavigationIcon(WallpaperBoardConfiguration.NavigationIcon.STYLE_4);
        configuration.setNavigationViewHeaderStyle(WallpaperBoardConfiguration.NavigationViewHeader.MINI);
        configuration.setWallpapersGridStyle(WallpaperBoardConfiguration.GridStyle.CARD);
        configuration.setCropWallpaperEnabledByDefault(true);
        configuration.setDashboardThemingEnabled(true);
        configuration.setLatestWallpapersDisplayMax(50);

        return configuration;
    }
}
