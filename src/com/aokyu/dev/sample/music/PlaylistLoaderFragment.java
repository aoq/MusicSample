/*
 * Copyright 2013 Yu AOKI
 */

package com.aokyu.dev.sample.music;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

public abstract class PlaylistLoaderFragment extends Fragment {

    public final class ColumnIndex {

        public static final int ID = 0;
        public static final int NAME = 1;

        private ColumnIndex() {}
    }

    protected Context mContext;
    private LoaderManager mLoaderManager;

    private PlaylistLoaderCallbacks mLoaderCallbacks;

    private static final int ID_PLAYLIST_LOADER = 0x00000004;

    protected abstract void onLoadFinished(Loader<Cursor> loader, Cursor cursor);
    protected abstract void onLoaderReset(Loader<Cursor> loader);

    public PlaylistLoaderFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
            mContext = activity.getApplicationContext();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoaderManager = getLoaderManager();
        mLoaderCallbacks = new PlaylistLoaderCallbacks(mContext, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadPlaylists();
    }

    protected void loadPlaylists() {
        Loader<Cursor> loader = mLoaderManager.getLoader(ID_PLAYLIST_LOADER);

        Bundle args = new Bundle();
        if (loader != null) {
            mLoaderManager.restartLoader(ID_PLAYLIST_LOADER, args, mLoaderCallbacks);
        } else {
            mLoaderManager.initLoader(ID_PLAYLIST_LOADER, args, mLoaderCallbacks);
        }
    }

    private static final class PlaylistCursorLoader extends CursorLoader {

        private static final String[] PROJECTION = new String[] {
            MediaStore.Audio.Playlists._ID,
            MediaStore.Audio.Playlists.NAME
        };

        public PlaylistCursorLoader(Context context) {
            super(context, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, PROJECTION, null, null, null);
        }
    }

    private static class PlaylistLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private WeakReference<Context> mContext;
        private WeakReference<PlaylistLoaderFragment> mFragment;

        public PlaylistLoaderCallbacks(Context context, PlaylistLoaderFragment fragment) {
            mContext = new WeakReference<Context>(context);
            mFragment = new WeakReference<PlaylistLoaderFragment>(fragment);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new PlaylistCursorLoader(mContext.get());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            PlaylistLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoadFinished(loader, cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            PlaylistLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoaderReset(loader);
            }
        }
    }
}
