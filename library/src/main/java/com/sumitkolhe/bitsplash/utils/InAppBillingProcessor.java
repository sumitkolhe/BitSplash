package com.sumitkolhe.bitsplash.utils;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.util.Log;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.utils.listeners.InAppBillingListener;

import java.lang.ref.WeakReference;


public class InAppBillingProcessor implements BillingProcessor.IBillingHandler {

    private Context mContext;
    private boolean mIsInitialized;
    private BillingProcessor mBillingProcessor;

    private static String mLicenseKey;
    private static WeakReference<InAppBillingProcessor> mInAppBilling;

    private InAppBillingProcessor(Context context) {
        mContext = context;
    }

    public void init(@NonNull String licenseKey) {
        mLicenseKey = licenseKey;

        if (mInAppBilling.get().mContext.getResources().getBoolean(R.bool.enable_donation)) {
            getProcessor();
        }
    }

    public BillingProcessor getProcessor() {
        if (mLicenseKey == null) {
            LogUtil.e("InAppBillingProcessor: license key is null, make sure to call InAppBillingProcessor.init() first");
        }

        if (mInAppBilling.get().mBillingProcessor == null || !mInAppBilling.get().mIsInitialized) {
            mInAppBilling.get().mBillingProcessor = new BillingProcessor(
                    mInAppBilling.get().mContext,
                    mLicenseKey,
                    mInAppBilling.get());
        }
        return mInAppBilling.get().mBillingProcessor;
    }

    public void destroy() {
        if (mInAppBilling != null && mInAppBilling.get() != null) {
            if (mInAppBilling.get().getProcessor() != null) {
                mInAppBilling.get().getProcessor().release();
            }

            mInAppBilling.clear();
        }
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mInAppBilling.get().getProcessor().handleActivityResult(requestCode, resultCode, data);
    }

    public static InAppBillingProcessor get(@NonNull Context context) {
        if (mInAppBilling == null || mInAppBilling.get() == null) {
            mInAppBilling = new WeakReference<>(new InAppBillingProcessor(context));
        }
        return mInAppBilling.get();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        if (mInAppBilling == null || mInAppBilling.get() == null) {
            LogUtil.e("InAppBillingProcessor error: not initialized");
            return;
        }

        try {
            InAppBillingListener listener = (InAppBillingListener) mInAppBilling.get().mContext;
            listener.onInAppBillingConsume(productId);
        } catch (Exception e) {
            LogUtil.e(Log.getStackTraceString(e));
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        if (mInAppBilling == null || mInAppBilling.get() == null) {
            LogUtil.e("InAppBillingProcessor error: not initialized");
            return;
        }

        if (errorCode == Constants.BILLING_ERROR_FAILED_TO_INITIALIZE_PURCHASE) {
            mInAppBilling.get().mIsInitialized = false;
        }
    }

    @Override
    public void onBillingInitialized() {
        if (mInAppBilling == null || mInAppBilling.get() == null) {
            LogUtil.e("InAppBillingProcessor error: not initialized");
            return;
        }

        mInAppBilling.get().mIsInitialized = true;
    }
}
