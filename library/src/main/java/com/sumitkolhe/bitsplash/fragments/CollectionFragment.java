package com.sumitkolhe.bitsplash.fragments;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.danimahardhika.android.helpers.animation.AnimationHelper;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.ViewHelper;
import com.danimahardhika.android.helpers.core.WindowHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.activities.WallpaperBoardActivity;
import com.sumitkolhe.bitsplash.activities.WallpaperBoardBrowserActivity;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.helpers.ConfigurationHelper;
import com.sumitkolhe.bitsplash.items.Collection;
import com.sumitkolhe.bitsplash.items.PopupItem;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.sumitkolhe.bitsplash.utils.Popup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CollectionFragment extends Fragment {

    @BindView(R2.id.search_bar)
    CardView mSearchBar;
    @BindView(R2.id.navigation)
    ImageView mNavigation;
    @BindView(R2.id.sort)
    ImageView mMenuSort;
    @BindView(R2.id.appbar)
    AppBarLayout mAppBar;
    @BindView(R2.id.toolbar)
    Toolbar mToolbar;
    @BindView(R2.id.tab)
    TabLayout mTab;
    @BindView(R2.id.pager)
    ViewPager mPager;

    private CollectionPagerAdapter mAdapter;

    private boolean mIsAppBarExpanded = false;
    private boolean mIsSearchBarShown = false;
    private int mSearchBarTranslationY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        ButterKnife.bind(this, view);
        initViewPager();
        mTab.setupWithViewPager(mPager);
        mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
                tab.setIcon(mAdapter.getIcon(tab.getPosition(), true));

                String tag = mAdapter.get(tab.getPosition()).getTag();
                if (tag.equals(Extras.TAG_CATEGORIES) || tag.equals(Extras.TAG_LATEST)) {
                    if (mMenuSort.getVisibility() == View.VISIBLE  ) {
                        //AnimationHelper.hide(mMenuSort).start();
                        mMenuSort.setVisibility(View.INVISIBLE);
                    }
                } else if (tag.equals(Extras.TAG_WALLPAPERS)) {
                    if (mMenuSort.getVisibility() == View.INVISIBLE) {
                        AnimationHelper.show(mMenuSort).start();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(mAdapter.getIcon(tab.getPosition(), false));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSearchBarTranslationY = getResources().getDimensionPixelSize(R.dimen.default_toolbar_height) +
                getResources().getDimensionPixelSize(R.dimen.content_margin) * 2;

        ViewHelper.setupToolbar(mToolbar);

        mToolbar.setTitle("");
        initAppBar();
        initSearchBar();

        for (int i = 0; i < mTab.getTabCount(); i++) {
            TabLayout.Tab tab = mTab.getTabAt(i);
            if (tab != null) {
                tab.setIcon(mAdapter.getIcon(i, i == 0));
            }
        }
    }
//change the order of tab elements here
    private void initViewPager() {
        List<Collection> collection = new ArrayList<>();

        collection.add(new Collection(R.drawable.ic_collection_categories,
                new CategoriesFragment(), Extras.TAG_CATEGORIES));
        collection.add(new Collection(R.drawable.ic_collection_wallpapers,
                new WallpapersFragment(), Extras.TAG_WALLPAPERS));
        collection.add(new Collection(R.drawable.ic_collection_latest,
                new LatestFragment(), Extras.TAG_LATEST));



        mPager.setOffscreenPageLimit(2);
        mAdapter = new CollectionPagerAdapter(getChildFragmentManager(), collection);
        mPager.setAdapter(mAdapter);
    }

    private void initAppBar() {
        mAppBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = mAppBar.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

            if (percentage == 1f) {
                if (mIsSearchBarShown) {
                    mIsSearchBarShown = false;
                    mSearchBar.animate().cancel();
                    mSearchBar.animate().translationY(-mSearchBarTranslationY)
                            .setInterpolator(new DecelerateInterpolator())
                            .setDuration(400)
                            .start();
                }
            } else if (percentage < 0.8f) {
                if (!mIsSearchBarShown) {
                    mIsSearchBarShown = true;
                    mSearchBar.animate().cancel();
                    mSearchBar.animate().translationY(0)
                            .setInterpolator(new LinearOutSlowInInterpolator())
                            .setDuration(400)
                            .start();
                }
            }

            if (percentage < 0.2f) {
                if (!mIsAppBarExpanded) {
                    mIsAppBarExpanded = true;
                    int color = ColorHelper.getAttributeColor(getActivity(), R.attr.colorPrimary);
                    ColorHelper.setupStatusBarIconColor(getActivity(), ColorHelper.isLightColor(color));
                }
            } else if (percentage == 1.0f) {
                if (mIsAppBarExpanded) {
                    mIsAppBarExpanded = false;
                    ColorHelper.setupStatusBarIconColor(getActivity(), false);
                }
            }
        });
    }

    private void initSearchBar() {
        Drawable drawable = ConfigurationHelper.getNavigationIcon(getActivity(),
                WallpaperBoardApplication.getConfig().getNavigationIcon());
        int color = ColorHelper.getAttributeColor(getActivity(), R.attr.search_bar_icon);
        if (drawable != null) {
            mNavigation.setImageDrawable(DrawableHelper.getTintedDrawable(drawable, color));
        }
        mNavigation.setOnClickListener(view -> {
            if (getActivity() instanceof WallpaperBoardActivity) {
                ((WallpaperBoardActivity) getActivity()).openDrawer();
            }
        });

        ImageView searchIcon = getActivity().findViewById(R.id.search);
        if (searchIcon != null) {
            searchIcon.setImageDrawable(DrawableHelper.getTintedDrawable(
                    getActivity(), R.drawable.ic_toolbar_search, color));
        }

        TextView searchBarTitle = getActivity().findViewById(R.id.search_bar_title);
        if (searchBarTitle != null) {
            if (WallpaperBoardApplication.getConfig().getAppLogoColor() != -1) {
                searchBarTitle.setTextColor(WallpaperBoardApplication.getConfig().getAppLogoColor());
            } else {
                searchBarTitle.setTextColor(ColorHelper.setColorAlpha(color, 0.9f));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mSearchBar.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mSearchBar.getLayoutParams();
                params.setMargins(params.leftMargin,
                        params.topMargin + WindowHelper.getStatusBarHeight(getActivity()),
                        params.leftMargin,
                        params.bottomMargin);
            }

            StateListAnimator stateListAnimator = AnimatorInflater
                    .loadStateListAnimator(getActivity(), R.animator.card_lift);
            mSearchBar.setStateListAnimator(stateListAnimator);
        }

        mSearchBar.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), WallpaperBoardBrowserActivity.class);
            intent.putExtra(Extras.EXTRA_FRAGMENT_ID, Extras.ID_WALLPAPER_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        mMenuSort.setImageDrawable(DrawableHelper.getTintedDrawable(
                getActivity(), R.drawable.ic_toolbar_sort, color));
        mMenuSort.setOnClickListener(view -> Popup.Builder(getActivity())
                .to(mMenuSort)
                .list(PopupItem.getSortItems(getActivity(), true))
                .callback((popup, position) -> {
                    Preferences.get(getActivity())
                            .setSortBy(popup.getItems().get(position).getType());

                    refreshWallpapers();
                    popup.dismiss();
                })
                .show());
    }

    public void refreshWallpapers() {
        if (mAdapter == null) return;

        int index = 1;
        if (index > mAdapter.getCount()) return;
        Fragment fragment = mAdapter.getItem(index);
        if (fragment != null && fragment instanceof WallpapersFragment) {
            ((WallpapersFragment) fragment).getWallpapers();
        }
    }

    public void refreshCategories() {
        if (mAdapter == null) return;

        int index = 2;
        if (index > mAdapter.getCount()) return;
        Fragment fragment = mAdapter.getItem(index);
        if (fragment != null && fragment instanceof CategoriesFragment) {
            ((CategoriesFragment) fragment).getCategories();
        }
    }

    private class CollectionPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Collection> mCollection;

        CollectionPagerAdapter(FragmentManager fm, @NonNull List<Collection> collection) {
            super(fm);
            mCollection = collection;
        }

        @Override
        public Fragment getItem(int position) {
            return mCollection.get(position).getFragment();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mCollection.size();
        }

        Collection get(int position) {
            return mCollection.get(position);
        }

        Drawable getIcon(int position, boolean selected) {
            int color = ColorHelper.getAttributeColor(getActivity(), R.attr.tab_icon);
            if (selected) {
                color = ColorHelper.getAttributeColor(getActivity(), R.attr.tab_icon_selected);
            }

            Drawable drawable = DrawableHelper.get(getActivity(), mCollection.get(position).getIcon());
            return DrawableHelper.getTintedDrawable(drawable, color);
        }
    }
}
