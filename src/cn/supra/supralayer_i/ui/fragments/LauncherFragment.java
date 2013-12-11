/*
 * Tint Browser for Android
 * 
 * Copyright (C) 2012 - to infinity and beyond J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package cn.supra.supralayer_i.ui.fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import cn.supra.supralayer_i.R;
import cn.supra.supralayer_i.SpaceActivity;
import cn.supra.supralayer_i.model.AppImpl;
import cn.supra.supralayer_i.model.BookmarkHistoryItem;
import cn.supra.supralayer_i.model.BookmarksAdapter;
import cn.supra.supralayer_i.providers.BookmarksProvider;
import cn.supra.supralayer_i.providers.BookmarksWrapper;
import cn.supra.supralayer_i.ui.UIManager;
import cn.supra.supralayer_i.util.ApplicationUtils;
import cn.supra.supralayer_i.util.Constants;

public class LauncherFragment extends BaseAppFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public interface OnStartPageItemClickedListener {
        public void onStartPageItemClicked(String url);
    }

    private View mParentView = null;

    private GridView mGrid;
    private BookmarksAdapter mAdapter;

    private OnStartPageItemClickedListener mListener = null;

    private OnSharedPreferenceChangeListener mPreferenceChangeListener;

    private UIManager mUIManager;
    private boolean mInitialized;

    private boolean mListShown = true;

public LauncherFragment() {
    mInitialized = false;
}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!mInitialized) {
            try {
                // mUIManager = ((UIManagerProvider) activity).getUIManager();
            } catch (ClassCastException e) {
                Log.e("StartPageFragment.onAttach()", e.getMessage());
            }

            mInitialized = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mParentView == null) {
            mParentView = inflater.inflate(ApplicationUtils.getStartPageLayout(getActivity()),
                    container, false);
            mGrid = (GridView) mParentView.findViewById(R.id.StartPageFragmentGrid);

            String[] from = new String[] {
                    BookmarksProvider.Columns.TITLE, BookmarksProvider.Columns.URL
            };
            int[] to = new int[] {
                    R.id.StartPageRowTitle, R.id.StartPageRowUrl
            };

            mAdapter = new BookmarksAdapter(
                    getActivity(),
                    R.layout.start_page_row,
                    null,
                    from,
                    to,
                    ApplicationUtils.getBookmarksThumbnailsDimensions(getActivity()),
                    R.drawable.browser_thumbnail);

            mGrid.setAdapter(mAdapter);

            mGrid.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    if (mListener != null) {
                        BookmarkHistoryItem item = BookmarksWrapper.getBookmarkById(getActivity()
                                .getContentResolver(), id);

                        if (item != null) {
                            mListener.onStartPageItemClicked(item.getUrl());
                        }
                    }
                }
            });

            // mGrid.setOnTouchListener(mUIManager);

            mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                        String key) {
                    if (Constants.PREFERENCE_START_PAGE_LIMIT.equals(key)) {
                        getLoaderManager().restartLoader(0, null, LauncherFragment.this);
                    }
                }
            };

            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        }
        
        mParentView.findViewById(R.id.handle_view_1).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ((SpaceActivity)getActivity()).getUIManager().openAPP(new AppImpl("baidu", "baidu"));
            }
        });

        return mParentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListShown(false, false);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int limit;
        try {
            limit = Integer.parseInt(
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                            Constants.PREFERENCE_START_PAGE_LIMIT,
                            Integer.toString(getResources().getInteger(
                                    R.integer.default_start_page_items_number))));
        } catch (Exception e) {
            limit = getResources().getInteger(R.integer.default_start_page_items_number);
        }

        return BookmarksWrapper.getCursorLoaderForStartPage(getActivity(), limit);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if (isResumed()) {
            setListShown(true, true);
        } else {
            setListShown(true, false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void setOnStartPageItemClickedListener(OnStartPageItemClickedListener listener) {
        mListener = listener;
    }

    private void setListShown(boolean shown, boolean animate) {

        if (mListShown == shown) {
            return;
        }

        mListShown = shown;

        if (shown) {
            if (animate) {
                mGrid.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        android.R.anim.fade_in));
            }
            mGrid.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mGrid.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        android.R.anim.fade_out));
            }
            mGrid.setVisibility(View.GONE);
        }
    }

}
