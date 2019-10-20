package com.sumitkolhe.bitsplash.items;

import androidx.annotation.Nullable;

import com.sumitkolhe.bitsplash.databases.Database;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;



public class Filter {

    private List<Options> mOptions;

    public Filter() {
        mOptions = new ArrayList<>();
    }

    public Filter add(Options options) {
        if (mOptions.contains(options)) {
            LogUtil.e("filter already contains options");
            return this;
        }
        mOptions.add(options);
        return this;
    }

    @Nullable
    public Options get(int index) {
        if (index < 0 || index > mOptions.size()) {
            LogUtil.e("filter: index out of bounds");
            return null;
        }
        return mOptions.get(index);
    }

    public int size() {
        return mOptions.size();
    }

    public static Options Create(Column column) {
        return new Options(column);
    }

    public static class Options {

        private Column mColumn;
        private String mQuery;

        private Options(Column column) {
            mColumn = column;
            mQuery = "";
        }

        public Options setQuery(String query) {
            mQuery = query;
            return this;
        }

        public Column getColumn() {
            return mColumn;
        }

        public String getQuery() {
            return mQuery;
        }

        @Override
        public boolean equals(Object object) {
            boolean equals = false;
            if (object != null && object instanceof Options) {
                equals = mColumn == ((Options) object).getColumn() &&
                        mQuery.equals(((Options) object).getQuery());
            }
            return equals;
        }
    }

    public enum Column {
        ID(Database.KEY_ID),
        NAME(Database.KEY_NAME),
        AUTHOR(Database.KEY_AUTHOR),
        CATEGORY(Database.KEY_CATEGORY);

        private String mName;

        Column(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }
}
