package com.aokyu.dev.sample.music;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

public abstract class TrackLoaderFragment extends Fragment {

    public final class ColumnIndex {

        public static final int ID = 0;
        public static final int TITLE = 1;
        public static final int ARTIST = 2;
        public static final int ALBUM = 3;
        public static final int DURATION = 4;

        private ColumnIndex() {}
    }

    public final class Argument {
        public static final String ARTIST_ID = "arg_artist_id";
        public static final String ALBUM_ID = "arg_album_id";
        public static final String PLAYLIST_ID = "arg_playlist_id";
    }

    protected static final long NO_ID = -2;

    protected Context mContext;
    private LoaderManager mLoaderManager;

    private TrackLoaderCallbacks mLoaderCallbacks;

    private static final int ID_TRACK_LOADER = 0x00000001;

    protected long mArtistId = NO_ID;
    protected long mAlbumId = NO_ID;

    protected abstract void onLoadFinished(Loader<Cursor> loader, Cursor cursor);
    protected abstract void onLoaderReset(Loader<Cursor> loader);

    public TrackLoaderFragment() {}

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
        mLoaderCallbacks = new TrackLoaderCallbacks(mContext, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        loadTracks(args);
    }

    protected void loadTracks(long artistId) {
        Bundle args = new Bundle();
        args.putLong(Argument.ARTIST_ID, artistId);
        loadTracks(args);
    }

    protected void loadTracks(long artistId, long albumId) {
        Bundle args = new Bundle();
        args.putLong(Argument.ARTIST_ID, artistId);
        args.putLong(Argument.ALBUM_ID, albumId);
        loadTracks(args);
    }

    protected void loadTracks(Bundle args) {
        Loader<Cursor> loader = mLoaderManager.getLoader(ID_TRACK_LOADER);

        if (loader != null) {
            mLoaderManager.restartLoader(ID_TRACK_LOADER, args, mLoaderCallbacks);
        } else {
            mLoaderManager.initLoader(ID_TRACK_LOADER, args, mLoaderCallbacks);
        }
    }

    public long getArtistId() {
        return mArtistId;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    private static final class TrackCursorLoader extends CursorLoader {

        private static final String[] PROJECTION = new String[] {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        };

        public TrackCursorLoader(Context context) {
            super(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION, null, null, null);
        }

        public TrackCursorLoader(Context context,
                String selection, String[] selectionArgs, String sortOrder) {
            super(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION,
                    selection, selectionArgs, sortOrder);
        }

        public TrackCursorLoader(Context context, long playlistId) {
            super(context, MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                    PROJECTION, null, null, null);
        }
    }

    private static class TrackLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private WeakReference<Context> mContext;
        private WeakReference<TrackLoaderFragment> mFragment;

        public TrackLoaderCallbacks(Context context, TrackLoaderFragment fragment) {
            mContext = new WeakReference<Context>(context);
            mFragment = new WeakReference<TrackLoaderFragment>(fragment);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (args == null) {
                return new TrackCursorLoader(mContext.get());
            }

            StringBuilder builder = new StringBuilder();
            List<String> argsList = new ArrayList<String>();
            if (args.containsKey(Argument.ARTIST_ID)) {
                long artistId = args.getLong(Argument.ARTIST_ID);
                builder.append(MediaStore.Audio.Media.ARTIST_ID + "=?");
                argsList.add(String.valueOf(artistId));
            } else if (args.containsKey(Argument.ALBUM_ID)) {
                if (argsList.size() > 0) {
                    builder.append(" AND ");
                }
                long albumId = args.getLong(Argument.ALBUM_ID);
                builder.append(MediaStore.Audio.Media.ALBUM_ID + "=?");
                argsList.add(String.valueOf(albumId));
            } else if (args.containsKey(Argument.PLAYLIST_ID)) {
                long playlistId = args.getLong(Argument.PLAYLIST_ID);
                return new TrackCursorLoader(mContext.get(), playlistId);
            } else {
                return new TrackCursorLoader(mContext.get());
            }

            String selection = builder.toString();
            String[] selectionArgs = argsList.toArray(new String[0]);
            return new TrackCursorLoader(mContext.get(), selection, selectionArgs, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            TrackLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoadFinished(loader, cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            TrackLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoaderReset(loader);
            }
        }
    }
}
