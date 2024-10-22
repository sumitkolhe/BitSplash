package com.sumitkolhe.bitsplash.fragments;

import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.ViewHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.adapters.LatestAdapter;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardConfiguration;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.items.Wallpaper;
import com.sumitkolhe.bitsplash.tasks.WallpapersLoaderTask;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.sumitkolhe.bitsplash.helpers.ViewHelper.resetViewBottomPadding;



public class LatestFragment extends Fragment implements WallpapersLoaderTask.Callback {

    @BindView(R2.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R2.id.swipe)
    SwipeRefreshLayout mSwipe;
    @BindView(R2.id.progress)
    MaterialProgressBar mProgress;

    private List<Wallpaper> mWallpapers;
    private LatestAdapter mAdapter;
    private AsyncTask mAsyncTask;
    private StaggeredGridLayoutManager mManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mManager = new StaggeredGridLayoutManager(
                getActivity().getResources().getInteger(R.integer.latest_wallpapers_column_count),
                StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mManager);

        mWallpapers = new ArrayList<>();
        mAdapter = new LatestAdapter(getActivity(), mWallpapers);
        mRecyclerView.setAdapter(mAdapter);

        mSwipe.setColorSchemeColors(ColorHelper.getAttributeColor(
                getActivity(), R.attr.colorAccent));
        mSwipe.setOnRefreshListener(() -> {
            if (mAsyncTask != null) {
                mSwipe.setRefreshing(false);
                return;
            }

            WallpapersLoaderTask.with(getActivity()).callback(this).start();
            mAsyncTask = new WallpapersLoader().execute();
        });

        resetRecyclerViewPadding();
        resetViewBottomPadding(mRecyclerView, true);

        mAsyncTask = new WallpapersLoader().execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mAsyncTask != null) return;
        if (mAdapter == null) return;

        int[] positions = mManager.findFirstVisibleItemPositions(null);

        int spanCount = getActivity().getResources().getInteger(
                R.integer.latest_wallpapers_column_count);
        ViewHelper.resetSpanCount(mRecyclerView, spanCount);
        resetRecyclerViewPadding();
        resetViewBottomPadding(mRecyclerView, true);

        mAdapter = new LatestAdapter(getActivity(), mWallpapers);
        mRecyclerView.setAdapter(mAdapter);

        if (positions.length > 0)
            mRecyclerView.scrollToPosition(positions[0]);
    }

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onFinished(boolean success) {
        if (getActivity() == null) return;
        if (getActivity().isFinishing()) return;
        if (!success) return;

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(Extras.TAG_COLLECTION);
        if (fragment != null && fragment instanceof CollectionFragment) {
            ((CollectionFragment) fragment).refreshWallpapers();
            ((CollectionFragment) fragment).refreshCategories();
        }
    }

    private void resetRecyclerViewPadding() {
        int spanCount = getActivity().getResources().getInteger(
                R.integer.latest_wallpapers_column_count);
        if (spanCount == 1) {
            mRecyclerView.setPadding(0, 0, 0, 0);
            return;
        }

        if (WallpaperBoardApplication.getConfig().getWallpapersGrid() ==
                WallpaperBoardConfiguration.GridStyle.FLAT) {
            int padding = getActivity().getResources().getDimensionPixelSize(R.dimen.card_margin);
            mRecyclerView.setPadding(padding, padding, 0, 0);
            return;
        }

        int paddingTop = getActivity().getResources().getDimensionPixelSize(R.dimen.card_margin_top);
        int paddingLeft = getActivity().getResources().getDimensionPixelSize(R.dimen.card_margin_right);
        mRecyclerView.setPadding(paddingLeft, paddingTop, 0, 0);
    }

    private class WallpapersLoader extends AsyncTask<Void, Integer, Boolean> {

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
                    mWallpapers = Database.get(getActivity()).getLatestWallpapers();

                    for (int i = 0; i < mWallpapers.size(); i++) {
                        publishProgress(i);
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
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            new WallpaperDimensionLoader(values[0]).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (getActivity() == null) return;
            if (getActivity().isFinishing()) return;

            mAsyncTask = null;
        }
    }

    private class WallpaperDimensionLoader extends AsyncTask<Void, Void, Boolean> {

        private int mPosition;

        private WallpaperDimensionLoader(int position) {
            mPosition = position;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    if (mWallpapers.get(mPosition).getDimensions() != null)
                        return true;

                    URL url = new URL(mWallpapers.get(mPosition).getUrl());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(10000);

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream stream = connection.getInputStream();

                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(stream, null, options);

                        ImageSize imageSize = new ImageSize(options.outWidth, options.outHeight);
                        mWallpapers.get(mPosition).setDimensions(imageSize);

                        Database.get(getActivity()).updateWallpaper(
                                mWallpapers.get(mPosition));
                        stream.close();
                        return true;
                    }
                    return false;
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

            mSwipe.setRefreshing(false);
            mProgress.setVisibility(View.GONE);
            if (aBoolean) {
                if (mPosition == (mWallpapers.size() - 1)) {
                    if (mAdapter != null) {
                        mAdapter.setWallpapers(mWallpapers);
                        return;
                    }

                    mAdapter = new LatestAdapter(getActivity(), mWallpapers);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }
    }
}
