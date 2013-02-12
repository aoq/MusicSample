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

public class TrackListFragment extends TrackLoaderFragment {

    /* package */ static final String TAG = TrackListFragment.class.getSimpleName();

    private ListView mTrackView;
    private TrackAdapter mTrackAdapter;

    private OnTrackItemClickListener mListener;

    public TrackListFragment() {
        super();
    }

    public static TrackListFragment newInstance() {
        TrackListFragment fragment = new TrackListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnTrackItemClickListener) {
            mListener = (OnTrackItemClickListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.track_panel, null);
        setupViews(contentView);
        return contentView;
    }

    private void setupViews(View rootView) {
        mTrackView = (ListView) rootView.findViewById(R.id.track_view);
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.empty_view);
        mTrackView.setEmptyView(progressBar);
        mTrackAdapter = new TrackAdapter(mContext, null);
        mTrackView.setAdapter(mTrackAdapter);
        mTrackView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    mListener.onTrackItemClick(id);
                }
            }
        });
    }

    @Override
    protected void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mTrackAdapter != null) {
            Cursor oldCursor = mTrackAdapter.swapCursor(cursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }

    @Override
    protected void onLoaderReset(Loader<Cursor> loader) {
        if (mTrackAdapter != null) {
            mTrackAdapter.swapCursor(null);
        }
    }

    private static class TrackAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        private static final class ViewCache {
            public final TextView titleView;
            public final TextView artistView;
            public final TextView albumView;
            public final TextView durationView;

            public ViewCache(View root) {
                titleView = (TextView) root.findViewById(R.id.title_view);
                artistView = (TextView) root.findViewById(R.id.artist_view);
                albumView = (TextView) root.findViewById(R.id.album_view);
                durationView = (TextView) root.findViewById(R.id.duration_view);
            }
        }

        public TrackAdapter(Context context, Cursor c) {
            super(context, c, false);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.track_list_item, null);
            view.setTag(new ViewCache(view));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewCache cache = (ViewCache) view.getTag();

            String title = cursor.getString(ColumnIndex.TITLE);
            cache.titleView.setText(title);

            String artist = cursor.getString(ColumnIndex.ARTIST);
            cache.artistView.setText(artist);

            String album = cursor.getString(ColumnIndex.ALBUM);
            cache.albumView.setText(album);

            long durationSec = cursor.getLong(ColumnIndex.DURATION);
            String duration = secondToFormattedString(durationSec);
            cache.durationView.setText(duration);
        }

        private String secondToFormattedString(long millis) {
            StringBuilder builder = new StringBuilder();
            long second = millis / 1000;
            long secondOdd = second % 60;
            long minute = second / 60;
            long minuteOdd = minute % 60;
            long hour = minute / 60;

            String formatted = null;
            if (hour != 0) {
                formatted = String.format("%02d:%02d:%02d", hour, minuteOdd, secondOdd);
            } else {
                formatted = String.format("%02d:%02d", minute, secondOdd);
            }
            builder.append(formatted);

            return builder.toString();
        }
    }

    public interface OnTrackItemClickListener {
        public void onTrackItemClick(long trackId);
    }
}
