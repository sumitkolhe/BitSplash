package com.sumitkolhe.bitsplash.fragments.dialogs;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.danimahardhika.android.helpers.animation.AnimationHelper;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.adapters.FilterAdapter;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.helpers.TypefaceHelper;
import com.sumitkolhe.bitsplash.items.Category;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;



public class FilterFragment extends DialogFragment implements View.OnClickListener {

    @BindView(R2.id.title)
    TextView mTitle;
    @BindView(R2.id.menu_select)
    ImageView mMenuSelect;
    @BindView(R2.id.listview)
    ListView mListView;
    @BindView(R2.id.progress)
    MaterialProgressBar mProgress;

    private FilterAdapter mAdapter;
    private AsyncTask mAsyncTask;
    private boolean mIsMuzei;

    private static final String MUZEI = "muzei";
    private static final String TAG = "com.dm.wallpaper.board.dialog.filter";

    private static FilterFragment newInstance(boolean isMuzei) {
        FilterFragment fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MUZEI, isMuzei);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void showFilterDialog(FragmentManager fm, boolean isMuzei) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.add(newInstance(isMuzei), TAG)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        try {
            ft.commit();
        } catch (IllegalStateException e) {
            ft.commitAllowingStateLoss();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.typeface(TypefaceHelper.getMedium(getActivity()), TypefaceHelper.getRegular(getActivity()));
        builder.customView(R.layout.fragment_filter, false);
        MaterialDialog dialog = builder.build();
        dialog.show();

        ButterKnife.bind(this, dialog);
        mTitle.setText(mIsMuzei ? R.string.muzei_category : R.string.wallpaper_filter);
        mMenuSelect.setOnClickListener(this);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsMuzei = getArguments().getBoolean(MUZEI);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mIsMuzei = savedInstanceState.getBoolean(MUZEI);
        }

        mAsyncTask = new CategoriesLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MUZEI, mIsMuzei);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.menu_select) {
            if (mAsyncTask != null) return;
            if (mAdapter != null) {
                mAdapter.setEnabled(false);
            }
            mAsyncTask = new SelectAllLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void initMenuSelect() {
        int color = ColorHelper.getAttributeColor(getActivity(), android.R.attr.textColorPrimary);
        boolean isAllSelected = mAdapter.getCount() == mAdapter.getSelectedCount();

        mMenuSelect.setImageDrawable(DrawableHelper.getTintedDrawable(getActivity(),
                isAllSelected ? R.drawable.ic_toolbar_select_all_selected : R.drawable.ic_toolbar_select_all,
                color));
        AnimationHelper.show(mMenuSelect).start();
    }

    private class SelectAllLoader extends AsyncTask<Void, Void, Boolean> {

        boolean isAllSelected;

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    isAllSelected = mAdapter.selectAll();
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
                int color = ColorHelper.getAttributeColor(getActivity(), android.R.attr.textColorPrimary);
                mMenuSelect.setImageDrawable(DrawableHelper.getTintedDrawable(getActivity(),
                        isAllSelected ? R.drawable.ic_toolbar_select_all_selected : R.drawable.ic_toolbar_select_all,
                        color));
                if (mAdapter != null) {
                    mAdapter.setEnabled(true);
                    mAdapter.notifyDataSetChanged();
                }

            }
        }
    }

    private class CategoriesLoader extends AsyncTask<Void, Void, Boolean> {

        List<Category> categories;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    categories = Database.get(getActivity()).getCategories();
                    for (Category category : categories) {
                        int count = Database.get(getActivity()).getCategoryCount(category.getName());
                        category.setCount(count);
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
            mProgress.setVisibility(View.GONE);
            if (aBoolean) {
                mAdapter = new FilterAdapter(getActivity(), categories, mIsMuzei);
                mListView.setAdapter(mAdapter);

                initMenuSelect();
            } else {
                dismiss();
            }
        }
    }
}
