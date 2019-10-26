package com.sumitkolhe.bitsplash.applications;

import androidx.annotation.NonNull;

import com.sumitkolhe.bitsplash.board.sample.R;
import com.sumitkolhe.bitsplash.utils.JsonStructure;

public class WallpaperBoard extends WallpaperBoardApplication {

    //to enable author name and wallpaper name remove comments from jsonstructure.java and jsonhelper.java

    @NonNull
    @Override
    public WallpaperBoardConfiguration onInit() {
        WallpaperBoardConfiguration configuration = new WallpaperBoardConfiguration();
        configuration.setNavigationIcon(WallpaperBoardConfiguration.NavigationIcon.STYLE_1);
        configuration.setNavigationViewHeaderStyle(WallpaperBoardConfiguration.NavigationViewHeader.NORMAL);
        configuration.setWallpapersGridStyle(WallpaperBoardConfiguration.GridStyle.CARD);
       // configuration.setCropWallpaperEnabledByDefault(true);
        configuration.setDashboardThemingEnabled(true);

        return configuration;
    }
}
