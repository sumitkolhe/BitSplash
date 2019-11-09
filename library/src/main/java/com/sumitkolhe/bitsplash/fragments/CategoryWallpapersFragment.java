package com.sumitkolhe.bitsplash.fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.ViewHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.adapters.WallpapersAdapter;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardConfiguration;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.items.Filter;
import com.sumitkolhe.bitsplash.items.PopupItem;
import com.sumitkolhe.bitsplash.items.Wallpaper;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.utils.AlphanumComparator;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.sumitkolhe.bitsplash.utils.Popup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sumitkolhe.bitsplash.helpers.ViewHelper.resetViewBottomPadding;



public class CategoryWallpapersFragment extends Fragment {

    @BindView(R2.id.appbar)
    AppBarLayout mAppBar;
    @BindView(R2.id.toolbar)
    Toolbar mToolbar;
    @BindView(R2.id.category)
    TextView mCategory;
    @BindView(R2.id.count)
    TextView mCount;
    @BindView(R2.id.search_result)
    TextView mSearchResult;
    @BindView(R2.id.recyclerview)
    RecyclerView mRecyclerView;

    private String mCategoryName;
    private int mCategoryCount;

    private SearchView mSearchView;
    private WallpapersAdapter mAdapter;
    private AsyncTask<Void, Void, Boolean> mAsyncTask;

    private boolean mIsAppBarExpanded = false;

    public static CategoryWallpapersFragment newInstance(String category, int count) {
        CategoryWallpapersFragment fragment = new CategoryWallpapersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Extras.EXTRA_CATEGORY, category);
        bundle.putInt(Extras.EXTRA_COUNT, count);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_wallpapers, container, false);
        ButterKnife.bind(this, view);

        if (!Preferences.get(getActivity()).isShadowEnabled()) {
            View shadow = view.findViewById(R.id.shadow);
            if (shadow != null) shadow.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategoryName = getArguments().getString(Extras.EXTRA_CATEGORY);
            mCategoryCount = getArguments().getInt(Extras.EXTRA_COUNT);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewHelper.setupToolbar(mToolbar);

        int color = ColorHelper.getAttributeColor(getActivity(), R.attr.toolbar_icon);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(DrawableHelper.getTintedDrawable(
                getActivity(), R.drawable.ic_toolbar_back, color));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        mCategory.setText(mCategoryName);
        String count = mCategoryCount +" "+
                getActivity().getResources().getString(R.string.navigation_view_wallpapers);
        mCount.setText(count);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                getActivity().getResources().getInteger(R.integer.wallpapers_column_count)));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        if (WallpaperBoardApplication.getConfig().getWallpapersGrid() ==
                WallpaperBoardConfiguration.GridStyle.FLAT) {
            int padding = getActivity().getResources().getDimensionPixelSize(R.dimen.card_margin);
            mRecyclerView.setPadding(padding, padding, 0, 0);
        }
        resetViewBottomPadding(mRecyclerView, true);

        initAppBar();

        color = ColorHelper.getAttributeColor(getActivity(), android.R.attr.textColorPrimary);
        Drawable drawable = DrawableHelper.getTintedDrawable(
                getActivity(), R.drawable.ic_toolbar_search, color);
        mSearchResult.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

        mAsyncTask = new WallpapersLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_wallpaper_search_sort, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        MenuItem sort = menu.findItem(R.id.menu_sort);

        int color = ColorHelper.getAttributeColor(getActivity(), R.attr.toolbar_icon);
        search.setIcon(DrawableHelper.getTintedDrawable(getActivity(),
                R.drawable.ic_toolbar_search, color));
        sort.setIcon(DrawableHelper.getTintedDrawable(getActivity(),
                R.drawable.ic_toolbar_sort, color));

        mSearchView = (SearchView) search.getActionView();
        mSearchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setQueryHint(getActivity().getResources().getString(R.string.menu_search));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        ViewHelper.setSearchViewTextColor(mSearchView, color);
        ViewHelper.setSearchViewBackgroundColor(mSearchView, Color.TRANSPARENT);
        ViewHelper.setSearchViewCloseIcon(mSearchView,
                DrawableHelper.getTintedDrawable(getActivity(), R.drawable.ic_toolbar_close, color));
        ViewHelper.setSearchViewSearchIcon(mSearchView, null);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String string) {
                filterSearch(string);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String string) {
                mSearchView.clearFocus();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        } else if (id == R.id.menu_sort) {
            View menuSort = mToolbar.findViewById(R.id.menu_sort);
            if (menuSort == null) return false;

            Popup.Builder(getActivity())
                    .to(menuSort)
                    .list(PopupItem.getSortItems(getActivity(), false))
                    .callback((popup, position) -> {
                        popup.dismiss();
                        mSearchView.clearFocus();

                        if (mAsyncTask != null) return;
                        mAsyncTask = new WallpapersSortLoader(popup.getItems().get(position).getType())
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewHelper.resetSpanCount(mRecyclerView, getActivity().getResources().getInteger(
                R.integer.wallpapers_column_count));
        resetViewBottomPadding(mRecyclerView, true);
    }

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    private void initAppBar() {
        mAppBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = mAppBar.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

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

    private void filterSearch(String query) {
        try {
            mAdapter.search(query);
            if (mAdapter.getItemCount() == 0) {
                String text = String.format(getActivity().getResources().getString(
                        R.string.search_result_empty), query);
                mSearchResult.setText(text);
                mSearchResult.setVisibility(View.VISIBLE);
            }
            else mSearchResult.setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.e(Log.getStackTraceString(e));
        }
    }

    private class WallpapersLoader extends AsyncTask<Void, Void, Boolean> {

        private List<Wallpaper> wallpapers;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wallpapers = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    Filter filter = new Filter();
                    filter.add(Filter.Create(Filter.Column.CATEGORY).setQuery(mCategoryName));

                    wallpapers = Database.get(getActivity()).getFilteredWallpapers(filter);
                    return true;
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (getActivity() == null) return;
            if (getActivity().isFinishing()) return;

            mAsyncTask = null;
            if (aBoolean) {
                setHasOptionsMenu(true);

                mAdapter = new WallpapersAdapter(getActivity(), wallpapers, false, true);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    private class WallpapersSortLoader extends AsyncTask<Void, Void, Boolean> {

        private List<Wallpaper> wallpapers;
        private PopupItem.Type type;

        private WallpapersSortLoader(PopupItem.Type type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mAdapter != null) {
                wallpapers = mAdapter.getWallpapers();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    if (wallpapers == null) {
                        Filter filter = new Filter();
                        filter.add(Filter.Create(Filter.Column.CATEGORY).setQuery(mCategoryName));
                        wallpapers = Database.get(getActivity()).getFilteredWallpapers(filter);
                    }

                    if (type == PopupItem.Type.SORT_LATEST) {
                        Collections.sort(wallpapers, Collections.reverseOrder(new AlphanumComparator() {

                            @Override
                            public int compare(Object o1, Object o2) {
                                String s1 = ((Wallpaper) o1).getAddedOn();
                                String s2 = ((Wallpaper) o2).getAddedOn();
                                return super.compare(s1, s2);
                            }
                        }));
                    } else if (type == PopupItem.Type.SORT_OLDEST) {
                        Collections.sort(wallpapers, new AlphanumComparator() {

                            @Override
                            public int compare(Object o1, Object o2) {
                                String s1 = ((Wallpaper) o1).getAddedOn();
                                String s2 = ((Wallpaper) o2).getAddedOn();
                                return super.compare(s1, s2);
                            }
                        });
                    } else if (type == PopupItem.Type.SORT_RANDOM) {
                        Collections.shuffle(wallpapers);
                    } else {
                        Collections.sort(wallpapers, new AlphanumComparator() {

                            @Override
                            public int compare(Object o1, Object o2) {
                                String s1 = ((Wallpaper) o1).getName();
                                String s2 = ((Wallpaper) o2).getName();
                                return super.compare(s1, s2);
                            }
                        });
                    }
                    return true;
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (getActivity() == null) return;
            if (getActivity().isFinishing()) return;

            mAsyncTask = null;
            if (aBoolean) {
                if (mAdapter != null) {
                    mAdapter.setWallpapers(wallpapers);
                }
            }
        }
    }
 }
