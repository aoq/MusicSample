package com.aokyu.dev.sample.music;

import android.app.Activity;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AlbumListFragment extends AlbumLoaderFragment {

    /* package */ static final String TAG = AlbumListFragment.class.getSimpleName();

    private ListView mAlbumView;
    private AlbumAdapter mAlbumAdapter;

    private OnAlbumItemClickListener mListener;

    public AlbumListFragment() {
        super();
    }

    public static AlbumListFragment newInstance() {
        AlbumListFragment fragment = new AlbumListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnAlbumItemClickListener) {
            mListener = (OnAlbumItemClickListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.album_panel, null);
        setupViews(contentView);
        return contentView;
    }

    private void setupViews(View rootView) {
        mAlbumView = (ListView) rootView.findViewById(R.id.album_view);
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.empty_view);
        mAlbumView.setEmptyView(progressBar);
        mAlbumAdapter = new AlbumAdapter(mContext, null);
        mAlbumView.setAdapter(mAlbumAdapter);
        mAlbumView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    Cursor cursor = mAlbumAdapter.getCursor();
                    // Note that the cursor position have been changed via CursorAdapter#getItemId(int).
                    String name = cursor.getString(ColumnIndex.ALBUM);
                    mListener.onAlbumItemClick(id, name);
                }
            }
        });
    }

    @Override
    protected void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mAlbumAdapter != null) {
            Cursor oldCursor = mAlbumAdapter.swapCursor(cursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }

    @Override
    protected void onLoaderReset(Loader<Cursor> loader) {
        if (mAlbumAdapter != null) {
            mAlbumAdapter.swapCursor(null);
        }
    }

    private static class AlbumAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        private static final class ViewCache {
            public final TextView titleView;
            public final TextView artistView;

            public ViewCache(View root) {
                titleView = (TextView) root.findViewById(R.id.title_view);
                artistView = (TextView) root.findViewById(R.id.artist_view);
            }
        }

        public AlbumAdapter(Context context, Cursor c) {
            super(context, c, false);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.album_list_item, null);
            view.setTag(new ViewCache(view));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewCache cache = (ViewCache) view.getTag();

            String title = cursor.getString(ColumnIndex.ALBUM);
            cache.titleView.setText(title);

            String artist = cursor.getString(ColumnIndex.ARTIST);
            cache.artistView.setText(artist);
        }
    }

    public interface OnAlbumItemClickListener {
        public void onAlbumItemClick(long albumId, String albumName);
    }
}
