/*
 * Copyright 2013 Yu AOKI
 */

package com.aokyu.dev.sample.music;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CategoryListFragment extends Fragment {

    /* package */ static final String TAG = CategoryListFragment.class.getSimpleName();

    private Context mContext;

    private ListView mCategoryView;
    private ArrayAdapter<String> mCategoryAdapter;

    private OnCategoryItemClickListener mListener;

    public CategoryListFragment() {
        super();
    }

    public static CategoryListFragment newInstance() {
        CategoryListFragment fragment = new CategoryListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
            mContext = activity.getApplicationContext();

            if (activity instanceof OnCategoryItemClickListener) {
                mListener = (OnCategoryItemClickListener) activity;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.category_panel, null);
        setupViews(contentView);
        return contentView;
    }

    private void setupViews(View rootView) {
        mCategoryView = (ListView) rootView.findViewById(R.id.category_view);
        Resources res = mContext.getResources();
        String[] entries = res.getStringArray(R.array.category_list);
        mCategoryAdapter = new ArrayAdapter<String>(mContext,
                R.layout.category_list_item, R.id.item_view, entries);
        mCategoryView.setAdapter(mCategoryAdapter);

        mCategoryView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    Category category = Category.valueOf(position);
                    if (category != null) {
                        mListener.OnCategoryItemClick(category);
                    }
                }
            }
        });
    }

    public enum Category {
        TRACK(0),
        ARTIST(1),
        ALBUM(2),
        PLAYLIST(3);

        private int mValue;

        private Category(int value) {
            mValue = value;
        }

        public int intValue() {
            return mValue;
        }

        public static Category valueOf(int value) {
            Category[] categories = Category.values();
            int size = categories.length;
            for (int i = 0; i < size; i++) {
                Category category = categories[i];
                if (category.ordinal() == value) {
                    return category;
                }
            }

            return null;
        }
    }

    public interface OnCategoryItemClickListener {
        public void OnCategoryItemClick(Category category);
    }
}
