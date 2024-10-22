package com.sumitkolhe.bitsplash.fragments;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.ViewHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.adapters.WallpapersAdapter;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardConfiguration;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.items.Wallpaper;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.tasks.WallpapersLoaderTask;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.sumitkolhe.bitsplash.helpers.ViewHelper.resetViewBottomPadding;



public class WallpapersFragment extends Fragment {

    @BindView(R2.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R2.id.swipe)
    SwipeRefreshLayout mSwipe;
    @BindView(R2.id.progress)
    MaterialProgressBar mProgress;

    private AsyncTask<Void, Void, Boolean> mAsyncTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallpapers, container, false);
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
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                getActivity().getResources().getInteger(R.integer.wallpapers_column_count)));
        mRecyclerView.setHasFixedSize(false);

        if (WallpaperBoardApplication.getConfig().getWallpapersGrid() ==
                WallpaperBoardConfiguration.GridStyle.FLAT) {
            int padding = getActivity().getResources().getDimensionPixelSize(R.dimen.card_margin);
            mRecyclerView.setPadding(padding, padding, 0, 0);
        }
        resetViewBottomPadding(mRecyclerView, true);

        mSwipe.setColorSchemeColors(ColorHelper.getAttributeColor(
                getActivity(), R.attr.colorAccent));
        mSwipe.setOnRefreshListener(() -> {
            if (mAsyncTask != null) {
                mSwipe.setRefreshing(false);
                return;
            }

            WallpapersLoaderTask.with(getActivity()).start();
            mAsyncTask = new WallpapersTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        });

        getWallpapers();
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

    public void getWallpapers() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }

        if (Database.get(getActivity()).getWallpapersCount() > 0) {
            mAsyncTask = new WallpapersTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return;
        }

        mAsyncTask = new WallpapersTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private class WallpapersTask extends AsyncTask<Void, Void, Boolean> {

        private List<Wallpaper> mWallpapers;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mSwipe.isRefreshing()) {
                mProgress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    mWallpapers = Database.get(getActivity()).getWallpapers();
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

            mSwipe.setRefreshing(false);
            mProgress.setVisibility(View.GONE);
            if (aBoolean) {
                WallpapersAdapter adapter = new WallpapersAdapter(getActivity(), mWallpapers, false, false);
                mRecyclerView.setAdapter(adapter);
            }
        }
    }
}
