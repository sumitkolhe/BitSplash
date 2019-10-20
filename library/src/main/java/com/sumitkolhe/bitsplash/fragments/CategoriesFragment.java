package com.sumitkolhe.bitsplash.fragments;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danimahardhika.android.helpers.core.ViewHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.adapters.CategoriesAdapter;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardConfiguration;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.items.Category;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.sumitkolhe.bitsplash.helpers.ViewHelper.resetViewBottomPadding;

public class CategoriesFragment extends Fragment {

    @BindView(R2.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R2.id.progress)
    MaterialProgressBar mProgress;

    private List<Category> mCategories;
    private GridLayoutManager mManager;
    private CategoriesAdapter mAdapter;
    private AsyncTask mAsyncTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mManager = new GridLayoutManager(getActivity(),
                getActivity().getResources().getInteger(R.integer.categories_column_count));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setHasFixedSize(false);

        resetRecyclerViewPadding();
        resetViewBottomPadding(mRecyclerView, true);

        mAdapter = new CategoriesAdapter(getActivity(), new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);

        getCategories();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mAsyncTask != null) return;
        if (mAdapter == null) return;

        int position = mManager.findFirstVisibleItemPosition();

        int spanCount = getActivity().getResources().getInteger(
                R.integer.categories_column_count);
        ViewHelper.resetSpanCount(mRecyclerView, spanCount);
        resetRecyclerViewPadding();
        resetViewBottomPadding(mRecyclerView, true);

        mAdapter = new CategoriesAdapter(getActivity(), mCategories);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.scrollToPosition(position);
    }

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    public void getCategories() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }

        if (Database.get(getActivity()).getWallpapersCount() > 0) {
            mAsyncTask = new CategoriesLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return;
        }

        mAsyncTask = new CategoriesLoader().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void resetRecyclerViewPadding() {
        int spanCount = getActivity().getResources().getInteger(
                R.integer.categories_column_count);
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

    private class CategoriesLoader extends AsyncTask<Void, Category, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() == null) return;
            if (getActivity().isFinishing()) return;

            mProgress.setVisibility(View.VISIBLE);

            if (mAdapter != null) {
                mAdapter.clear();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    mCategories = Database.get(getActivity()).getCategories();
                    for (Category category : mCategories) {
                        category = Database.get(getActivity()).getCategoryPreview(category);
                        publishProgress(category);
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
        protected void onProgressUpdate(Category... values) {
            super.onProgressUpdate(values);
            if (getActivity() == null) return;
            if (getActivity().isFinishing()) return;

            if (mAdapter != null && values.length > 0) {
                mAdapter.add(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (getActivity() == null) return;
            if (getActivity().isFinishing()) return;

            mAsyncTask = null;
            mProgress.setVisibility(View.GONE);
            if (aBoolean) {
                if (mAdapter == null) {
                    mAdapter = new CategoriesAdapter(getActivity(), mCategories);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }
    }
}
