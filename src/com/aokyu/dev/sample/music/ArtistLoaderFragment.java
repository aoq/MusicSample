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

public abstract class ArtistLoaderFragment extends Fragment {

    public final class ColumnIndex {

        public static final int ID = 0;
        public static final int ARTIST = 1;
        public static final int NUMBER_OF_TRACKS = 2;

        private ColumnIndex() {}
    }

    protected Context mContext;
    private LoaderManager mLoaderManager;

    private ArtistLoaderCallbacks mLoaderCallbacks;

    private static final int ID_ARTIST_LOADER = 0x00000002;

    protected abstract void onLoadFinished(Loader<Cursor> loader, Cursor cursor);
    protected abstract void onLoaderReset(Loader<Cursor> loader);

    public ArtistLoaderFragment() {}

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
        mLoaderCallbacks = new ArtistLoaderCallbacks(mContext, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadArtists();
    }

    protected void loadArtists() {
        Loader<Cursor> loader = mLoaderManager.getLoader(ID_ARTIST_LOADER);

        Bundle args = new Bundle();
        if (loader != null) {
            mLoaderManager.restartLoader(ID_ARTIST_LOADER, args, mLoaderCallbacks);
        } else {
            mLoaderManager.initLoader(ID_ARTIST_LOADER, args, mLoaderCallbacks);
        }
    }

    private static final class ArtistCursorLoader extends CursorLoader {

        private static final String[] PROJECTION = new String[] {
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };

        public ArtistCursorLoader(Context context) {
            super(context, MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, PROJECTION, null, null, null);
        }
    }

    private static class ArtistLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private WeakReference<Context> mContext;
        private WeakReference<ArtistLoaderFragment> mFragment;

        public ArtistLoaderCallbacks(Context context, ArtistLoaderFragment fragment) {
            mContext = new WeakReference<Context>(context);
            mFragment = new WeakReference<ArtistLoaderFragment>(fragment);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new ArtistCursorLoader(mContext.get());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            ArtistLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoadFinished(loader, cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            ArtistLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoaderReset(loader);
            }
        }
        
    }
}
