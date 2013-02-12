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

public abstract class AlbumLoaderFragment extends Fragment {

    public final class ColumnIndex {

        public static final int ID = 0;
        public static final int ALBUM = 1;
        public static final int ARTIST = 2;
        public static final int NUMBER_OF_SONGS = 3;

        private ColumnIndex() {}
    }

    protected Context mContext;
    private LoaderManager mLoaderManager;

    private AlbumLoaderCallbacks mLoaderCallbacks;

    private static final int ID_ALBUM_LOADER = 0x00000003;

    protected abstract void onLoadFinished(Loader<Cursor> loader, Cursor cursor);
    protected abstract void onLoaderReset(Loader<Cursor> loader);

    public AlbumLoaderFragment() {}

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
        mLoaderCallbacks = new AlbumLoaderCallbacks(mContext, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadAlbums();
    }

    protected void loadAlbums() {
        Loader<Cursor> loader = mLoaderManager.getLoader(ID_ALBUM_LOADER);

        Bundle args = new Bundle();
        if (loader != null) {
            mLoaderManager.restartLoader(ID_ALBUM_LOADER, args, mLoaderCallbacks);
        } else {
            mLoaderManager.initLoader(ID_ALBUM_LOADER, args, mLoaderCallbacks);
        }
    }

    private static final class AlbumCursorLoader extends CursorLoader {

        private static final String[] PROJECTION = new String[] {
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        };

        public AlbumCursorLoader(Context context) {
            super(context, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, PROJECTION, null, null, null);
        }
    }

    private static class AlbumLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private WeakReference<Context> mContext;
        private WeakReference<AlbumLoaderFragment> mFragment;

        public AlbumLoaderCallbacks(Context context, AlbumLoaderFragment fragment) {
            mContext = new WeakReference<Context>(context);
            mFragment = new WeakReference<AlbumLoaderFragment>(fragment);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AlbumCursorLoader(mContext.get());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            AlbumLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoadFinished(loader, cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            AlbumLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoaderReset(loader);
            }
        }

    }
}
