package com.aokyu.dev.sample.music;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

import com.aokyu.dev.sample.music.AlbumListFragment.OnAlbumItemClickListener;
import com.aokyu.dev.sample.music.ArtistListFragment.OnArtistItemClickListener;
import com.aokyu.dev.sample.music.CategoryListFragment.Category;
import com.aokyu.dev.sample.music.CategoryListFragment.OnCategoryItemClickListener;
import com.aokyu.dev.sample.music.TrackListFragment.OnTrackItemClickListener;

public class MusicActivity extends Activity
    implements OnCategoryItemClickListener, OnTrackItemClickListener,
        OnArtistItemClickListener, OnAlbumItemClickListener {

    private boolean mTransactionAllowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_screen);

        mTransactionAllowed = true;
        showCategoryListFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTransactionAllowed = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTransactionAllowed = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mTransactionAllowed = false;
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.music_screen, menu);
        return true;
    }

    private void showCategoryListFragment() {
        FragmentManager manager = getFragmentManager();

        CategoryListFragment fragment =
                (CategoryListFragment) manager.findFragmentByTag(CategoryListFragment.TAG);
        if (fragment == null) {
            fragment = CategoryListFragment.newInstance();
        }

        showFragment(fragment);
    }

    private void showAllTracksListFragment() {
        FragmentManager manager = getFragmentManager();

        TrackListFragment fragment =
                (TrackListFragment) manager.findFragmentByTag(TrackListFragment.TAG);
        if (fragment == null) {
            fragment = TrackListFragment.newInstance();
        }

        showFragment(fragment, TrackListFragment.TAG);
    }

    private void showArtistListFragment() {
        FragmentManager manager = getFragmentManager();

        ArtistListFragment fragment =
                (ArtistListFragment) manager.findFragmentByTag(ArtistListFragment.TAG);
        if (fragment == null) {
            fragment = ArtistListFragment.newInstance();
        }

        showFragment(fragment, ArtistListFragment.TAG);
    }

    private void showTracksListFragmentByArtist(long artistId) {
        FragmentManager manager = getFragmentManager();

        TrackListFragment fragment =
                (TrackListFragment) manager.findFragmentByTag(TrackListFragment.TAG);
        if (fragment == null) {
            fragment = TrackListFragment.newInstance();
        }

        Bundle args = new Bundle();
        args.putLong(TrackListFragment.Argument.ARTIST_ID, artistId);
        fragment.setArguments(args);
        showFragment(fragment, TrackListFragment.TAG);
    }

    private void showAlbumListFragment() {
        FragmentManager manager = getFragmentManager();

        AlbumListFragment fragment =
                (AlbumListFragment) manager.findFragmentByTag(AlbumListFragment.TAG);
        if (fragment == null) {
            fragment = AlbumListFragment.newInstance();
        }

        showFragment(fragment, AlbumListFragment.TAG);
    }

    private void showTracksListFragmentByAlbum(long albumId) {
        FragmentManager manager = getFragmentManager();

        TrackListFragment fragment =
                (TrackListFragment) manager.findFragmentByTag(TrackListFragment.TAG);
        if (fragment == null) {
            fragment = TrackListFragment.newInstance();
        }

        Bundle args = new Bundle();
        args.putLong(TrackListFragment.Argument.ALBUM_ID, albumId);
        fragment.setArguments(args);
        showFragment(fragment, TrackListFragment.TAG);
    }

    private void showFragment(Fragment fragment) {
        if (!isFragmentTransactionAllowed()) {
            return;
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_view, fragment);
        transaction.commit();
    }

    private void showFragment(Fragment fragment, String tag) {
        if (!isFragmentTransactionAllowed()) {
            return;
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(tag);
        transaction.replace(R.id.container_view, fragment, tag);
        transaction.commit();
    }

    public boolean isFragmentTransactionAllowed() {
        return mTransactionAllowed;
    }

    @Override
    public void OnCategoryItemClick(Category category) {
        switch (category) {
        case TRACK:
            showAllTracksListFragment();
            break;
        case ARTIST:
            showArtistListFragment();
            break;
        case ALBUM:
            showAlbumListFragment();
            break;
        case PLAYLIST:
            break;
        }
    }

    @Override
    public void onTrackItemClick(long trackId) {
        // TODO
    }

    @Override
    public void onArtistItemClick(long artistId) {
        showTracksListFragmentByArtist(artistId);
    }

    @Override
    public void onAlbumItemClick(long albumId) {
        showTracksListFragmentByAlbum(albumId);
    }

}
