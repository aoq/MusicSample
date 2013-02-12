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
import android.widget.TextView;

public class PlaylistListFragment extends PlaylistLoaderFragment {

    /* package */ static final String TAG = PlaylistListFragment.class.getSimpleName();

    private ListView mPlaylistView;
    private PlaylistAdapter mPlaylistAdapter;

    private OnPlaylistItemClickListener mListener;

    public PlaylistListFragment() {
        super();
    }

    public static PlaylistListFragment newInstance() {
        PlaylistListFragment fragment = new PlaylistListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnPlaylistItemClickListener) {
            mListener = (OnPlaylistItemClickListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.playlist_panel, null);
        setupViews(contentView);
        return contentView;
    }

    private void setupViews(View rootView) {
        mPlaylistView = (ListView) rootView.findViewById(R.id.playlist_view);
        mPlaylistAdapter = new PlaylistAdapter(mContext, null);
        mPlaylistView.setAdapter(mPlaylistAdapter);
        mPlaylistView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    Cursor cursor = mPlaylistAdapter.getCursor();
                    // Note that the cursor position have been changed via CursorAdapter#getItemId(int).
                    String name = cursor.getString(ColumnIndex.NAME);
                    mListener.onPlaylistItemClick(id, name);
                }
            }
        });
    }

    @Override
    protected void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mPlaylistAdapter != null) {
            Cursor oldCursor = mPlaylistAdapter.swapCursor(cursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }

    @Override
    protected void onLoaderReset(Loader<Cursor> loader) {
        if (mPlaylistAdapter != null) {
            mPlaylistAdapter.swapCursor(null);
        }
    }

    private static class PlaylistAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        private static final class ViewCache {
            public final TextView nameView;

            public ViewCache(View root) {
                nameView = (TextView) root.findViewById(R.id.name_view);
            }
        }

        public PlaylistAdapter(Context context, Cursor c) {
            super(context, c, false);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.playlist_list_item, null);
            view.setTag(new ViewCache(view));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewCache cache = (ViewCache) view.getTag();

            String title = cursor.getString(ColumnIndex.NAME);
            cache.nameView.setText(title);
        }
    }

    public interface OnPlaylistItemClickListener {
        public void onPlaylistItemClick(long playlistId, String playlistName);
    }
}
