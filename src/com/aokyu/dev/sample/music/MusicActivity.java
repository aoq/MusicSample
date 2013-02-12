/*
 * Copyright 2013 Yu AOKI
 */

package com.aokyu.dev.sample.music;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentBreadCrumbs;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;

import com.aokyu.dev.sample.music.AlbumListFragment.OnAlbumItemClickListener;
import com.aokyu.dev.sample.music.ArtistListFragment.OnArtistItemClickListener;
import com.aokyu.dev.sample.music.CategoryListFragment.Category;
import com.aokyu.dev.sample.music.CategoryListFragment.OnCategoryItemClickListener;
import com.aokyu.dev.sample.music.PlaylistListFragment.OnPlaylistItemClickListener;
import com.aokyu.dev.sample.music.TrackListFragment.OnTrackItemClickListener;

public class MusicActivity extends Activity
    implements OnCategoryItemClickListener, OnTrackItemClickListener,
        OnArtistItemClickListener, OnAlbumItemClickListener,
        OnPlaylistItemClickListener {

    private FragmentBreadCrumbs mBreadCrumbs;
    private boolean mTransactionAllowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_screen);

        mBreadCrumbs = (FragmentBreadCrumbs) findViewById(R.id.bread_crumbs);
        mBreadCrumbs.setActivity(this);
        Resources res = getResources();
        String title = res.getString(R.string.category_title);
        mBreadCrumbs.setTitle(title, title);

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

        Resources res = getResources();
        String title = res.getString(R.string.category_track);
        showFragment(fragment, TrackListFragment.TAG, title);
    }

    private void showArtistListFragment() {
        FragmentManager manager = getFragmentManager();

        ArtistListFragment fragment =
                (ArtistListFragment) manager.findFragmentByTag(ArtistListFragment.TAG);
        if (fragment == null) {
            fragment = ArtistListFragment.newInstance();
        }

        Resources res = getResources();
        String title = res.getString(R.string.category_artist);
        showFragment(fragment, ArtistListFragment.TAG, title);
    }

    private void showTracksListFragmentByArtist(long artistId, String artistName) {
        FragmentManager manager = getFragmentManager();

        TrackListFragment fragment =
                (TrackListFragment) manager.findFragmentByTag(TrackListFragment.TAG);
        if (fragment == null) {
            fragment = TrackListFragment.newInstance();
        }

        Bundle args = new Bundle();
        args.putLong(TrackListFragment.Argument.ARTIST_ID, artistId);
        fragment.setArguments(args);
        showFragment(fragment, TrackListFragment.TAG, artistName);
    }

    private void showAlbumListFragment() {
        FragmentManager manager = getFragmentManager();

        AlbumListFragment fragment =
                (AlbumListFragment) manager.findFragmentByTag(AlbumListFragment.TAG);
        if (fragment == null) {
            fragment = AlbumListFragment.newInstance();
        }

        Resources res = getResources();
        String title = res.getString(R.string.category_album);
        showFragment(fragment, AlbumListFragment.TAG, title);
    }

    private void showTracksListFragmentByAlbum(long albumId, String albumName) {
        FragmentManager manager = getFragmentManager();

        TrackListFragment fragment =
                (TrackListFragment) manager.findFragmentByTag(TrackListFragment.TAG);
        if (fragment == null) {
            fragment = TrackListFragment.newInstance();
        }

        Bundle args = new Bundle();
        args.putLong(TrackListFragment.Argument.ALBUM_ID, albumId);
        fragment.setArguments(args);
        showFragment(fragment, TrackListFragment.TAG, albumName);
    }

    private void showPlaylistListFragment() {
        FragmentManager manager = getFragmentManager();

        PlaylistListFragment fragment =
                (PlaylistListFragment) manager.findFragmentByTag(PlaylistListFragment.TAG);
        if (fragment == null) {
            fragment = PlaylistListFragment.newInstance();
        }

        Resources res = getResources();
        String title = res.getString(R.string.category_playlist);
        showFragment(fragment, PlaylistListFragment.TAG, title);
    }

    private void showTracksListFragmentByPlaylist(long playlistId, String playlistName) {
        FragmentManager manager = getFragmentManager();

        TrackListFragment fragment =
                (TrackListFragment) manager.findFragmentByTag(TrackListFragment.TAG);
        if (fragment == null) {
            fragment = TrackListFragment.newInstance();
        }

        Bundle args = new Bundle();
        args.putLong(TrackListFragment.Argument.PLAYLIST_ID, playlistId);
        fragment.setArguments(args);
        showFragment(fragment, TrackListFragment.TAG, playlistName);
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

    private void showFragment(Fragment fragment, String tag, String title) {
        if (!isFragmentTransactionAllowed()) {
            return;
        }

        String shortTitle = title;
        if (title.length() > 12) {
            StringBuilder builder = new StringBuilder();
            builder.append(title.substring(0, 12));
            builder.append("...");
            shortTitle = builder.toString();
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(tag);
        transaction.setBreadCrumbTitle(shortTitle);
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
            showPlaylistListFragment();
            break;
        }
    }

    @Override
    public void onTrackItemClick(long trackId) {
        // TODO
    }

    @Override
    public void onArtistItemClick(long artistId, String artistName) {
        showTracksListFragmentByArtist(artistId, artistName);
    }

    @Override
    public void onAlbumItemClick(long albumId, String albumName) {
        showTracksListFragmentByAlbum(albumId, albumName);
    }

    @Override
    public void onPlaylistItemClick(long playlistId, String playlistName) {
        showTracksListFragmentByPlaylist(playlistId, playlistName);
    }

}
