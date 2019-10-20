package com.sumitkolhe.bitsplash.fragments.dialogs;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.SkuDetails;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.sumitkolhe.bitsplash.adapters.InAppBillingAdapter;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.helpers.TypefaceHelper;
import com.sumitkolhe.bitsplash.items.InAppBilling;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.sumitkolhe.bitsplash.utils.InAppBillingProcessor;
import com.sumitkolhe.bitsplash.utils.listeners.InAppBillingListener;

import butterknife.BindView;
import butterknife.ButterKnife;



public class InAppBillingFragment extends DialogFragment {

    @BindView(R2.id.listview)
    ListView mListView;
    @BindView(R2.id.progress)
    ProgressBar mProgress;

    private String[] mProductsId;
    private InAppBillingAdapter mAdapter;
    private AsyncTask mAsyncTask;

    private static final String TAG = "com.dm.wallpaper.board.dialog.inappbilling";

    private static InAppBillingFragment newInstance(String key, String[] productId) {
        InAppBillingFragment fragment = new InAppBillingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Extras.EXTRA_KEY, key);
        bundle.putStringArray(Extras.EXTRA_PRODUCT_ID, productId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void showInAppBillingDialog(@NonNull FragmentManager fm, @NonNull String key, @NonNull String[] productId) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.add(newInstance(key, productId), TAG)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        try {
            ft.commit();
        } catch (IllegalStateException e) {
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductsId = getArguments().getStringArray(Extras.EXTRA_PRODUCT_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.customView(R.layout.fragment_inappbilling, false)
                .typeface(TypefaceHelper.getBold(getActivity()), TypefaceHelper.getBold(getActivity()))
                .title(R.string.navigation_view_donate)
                .positiveText(R.string.donate)
                .negativeText(R.string.close)
                .onPositive((dialog, which) -> {
                    if (mAsyncTask == null) {
                        try {
                            InAppBillingListener listener = (InAppBillingListener) getActivity();
                            listener.onInAppBillingSelected(mAdapter.getSelectedProduct());
                        } catch (Exception ignored) {}
                        dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        setCancelable(false);

        ButterKnife.bind(this, dialog);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mProductsId = savedInstanceState.getStringArray(Extras.EXTRA_PRODUCT_ID);
        }

        mAsyncTask = new InAppProductsLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(Extras.EXTRA_PRODUCT_ID, mProductsId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    private class InAppProductsLoader extends AsyncTask<Void, Void, Boolean> {

        InAppBilling[] inAppBillings;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
            inAppBillings = new InAppBilling[mProductsId.length];
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    for (int i = 0; i < mProductsId.length; i++) {
                        SkuDetails product = InAppBillingProcessor.get(getActivity()).getProcessor()
                                .getPurchaseListingDetails(mProductsId[i]);
                        if (product != null) {
                            InAppBilling inAppBilling;
                            String title = product.title.substring(0, product.title.lastIndexOf("("));
                            inAppBilling = new InAppBilling(product.priceText, mProductsId[i], title);
                            inAppBillings[i] = inAppBilling;
                        } else {
                            if (i == mProductsId.length - 1)
                                return false;
                        }
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
                mAdapter = new InAppBillingAdapter(getActivity(), inAppBillings);
                mListView.setAdapter(mAdapter);
            } else {
                dismiss();
                Toast.makeText(getActivity(), R.string.billing_load_product_failed,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
