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

public class ArtistListFragment extends ArtistLoaderFragment {

    /* package */ static final String TAG = ArtistListFragment.class.getSimpleName();

    private ListView mArtistView;
    private ArtistAdapter mArtistAdapter;

    private OnArtistItemClickListener mListener;

    public ArtistListFragment() {
        super();
    }

    public static ArtistListFragment newInstance() {
        ArtistListFragment fragment = new ArtistListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnArtistItemClickListener) {
            mListener = (OnArtistItemClickListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.artist_panel, null);
        setupViews(contentView);
        return contentView;
    }

    private void setupViews(View rootView) {
        mArtistView = (ListView) rootView.findViewById(R.id.artist_view);
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.empty_view);
        mArtistView.setEmptyView(progressBar);
        mArtistAdapter = new ArtistAdapter(mContext, null);
        mArtistView.setAdapter(mArtistAdapter);
        mArtistView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    Cursor cursor = mArtistAdapter.getCursor();
                    // Note that the cursor position have been changed via CursorAdapter#getItemId(int).
                    String name = cursor.getString(ColumnIndex.ARTIST);
                    mListener.onArtistItemClick(id, name);
                }
            }
        });
    }

    @Override
    protected void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mArtistAdapter != null) {
            Cursor oldCursor = mArtistAdapter.swapCursor(cursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }

    @Override
    protected void onLoaderReset(Loader<Cursor> loader) {
        if (mArtistAdapter != null) {
            mArtistAdapter.swapCursor(null);
        }
    }

    private static class ArtistAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        private static final class ViewCache {
            public final TextView nameView;

            public ViewCache(View root) {
                nameView = (TextView) root.findViewById(R.id.name_view);
            }
        }

        public ArtistAdapter(Context context, Cursor c) {
            super(context, c, false);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.artist_list_item, null);
            view.setTag(new ViewCache(view));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewCache cache = (ViewCache) view.getTag();

            String artistName = cursor.getString(ColumnIndex.ARTIST);
            cache.nameView.setText(artistName);
        }
    }

    public interface OnArtistItemClickListener {
        public void onArtistItemClick(long artistId, String artistName);
    }
}
