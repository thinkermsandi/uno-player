package za.co.rationalthinkers.unoplayer.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.config.Constants;
import za.co.rationalthinkers.unoplayer.android.fragment.AlbumDetailFragment;
import za.co.rationalthinkers.unoplayer.android.fragment.AlbumsFragment;
import za.co.rationalthinkers.unoplayer.android.fragment.ArtistDetailFragment;
import za.co.rationalthinkers.unoplayer.android.fragment.ArtistsFragment;
import za.co.rationalthinkers.unoplayer.android.fragment.MusicFragment;
import za.co.rationalthinkers.unoplayer.android.fragment.SongsFragment;
import za.co.rationalthinkers.unoplayer.android.model.Album;
import za.co.rationalthinkers.unoplayer.android.model.Artist;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.util.MusicPlayerRemote;
import za.co.rationalthinkers.unoplayer.android.util.NavigationUtils;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.TAG_FRAGMENT_ALBUM_DETAILS;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.TAG_FRAGMENT_ARTIST_DETAILS;

public class MusicActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ImageView.OnClickListener,
        MusicFragment.OnMusicActionListener,
        AlbumsFragment.OnAlbumsActionListener,
        AlbumDetailFragment.OnAlbumDetailActionListener,
        ArtistsFragment.OnArtistsActionListener,
        ArtistDetailFragment.OnArtistDetailActionListener,
        SongsFragment.OnSongsActionListener {

    private Context context;
    private FragmentManager fragmentManager;

    //UI References
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private ImageView toolbarSearchView;
    private TextView toolbarTitleView;
    private ImageView toolbarNowPlayingView;
    private ImageView toolbarOptionsView;
    private FrameLayout fragmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        context = getApplicationContext();
        fragmentManager = getSupportFragmentManager();

        initUI();
        setUpActionBar();
        setUpNavigationDrawer();
        setUpListeners();

        goToMusicFragment();
    }

    private void initUI(){
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbarSearchView = findViewById(R.id.toolbar_search);
        toolbarTitleView = findViewById(R.id.toolbar_title);
        toolbarNowPlayingView = findViewById(R.id.toolbar_now_playing);
        toolbarOptionsView = findViewById(R.id.toolbar_options);
        fragmentView = findViewById(R.id.playlists_fragment);
    }

    private void setUpActionBar(){
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    private void setUpNavigationDrawer(){
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setUpListeners(){
        navigationView.setNavigationItemSelectedListener(this);
        toolbarNowPlayingView.setOnClickListener(this);
    }

    private void goToMusicFragment(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.music_fragment, MusicFragment.newInstance());
        fragmentTransaction.commit();
    }

    private void goToAlbumDetailsFragment(Album album){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.music_fragment, AlbumDetailFragment.newInstance(album));
        fragmentTransaction.addToBackStack(TAG_FRAGMENT_ALBUM_DETAILS);
        fragmentTransaction.commit();
    }

    private void goToArtistDetailsFragment(Artist artist){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.music_fragment, ArtistDetailFragment.newInstance(artist));
        fragmentTransaction.addToBackStack(TAG_FRAGMENT_ARTIST_DETAILS);
        fragmentTransaction.commit();
    }

    private void goToMusicPlayerActivity(ArrayList<Song> songList, Song songToPlay){
        Intent musicPlayerIntent = new Intent(context, MusicPlayerActivity.class);
        musicPlayerIntent.putParcelableArrayListExtra(Constants.ARG_SONGS, songList);
        musicPlayerIntent.putExtra(Constants.ARG_SONG, songToPlay);
        startActivity(musicPlayerIntent);
    }

    private void closeFragment(){
        //Fragment finished or user pressed an exit button

        if (fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStackImmediate();
            return;
        }

        //else, recreate music fragment
        goToMusicFragment();
    }

    @Override
    public void onAlbumSelected(Album album) {
        goToAlbumDetailsFragment(album);
    }

    @Override
    public void onAlbumSongSelected(ArrayList<Song> songs, Song song) {
        goToMusicPlayerActivity(songs, song);
    }

    @Override
    public void onAlbumDetailExit() {
        closeFragment();
    }

    @Override
    public void onArtistSelected(Artist artist) {
        goToArtistDetailsFragment(artist);
    }

    @Override
    public void onArtistAlbumSelected(Album album) {
        goToAlbumDetailsFragment(album);
    }

    @Override
    public void onArtistSongSelected(ArrayList<Song> songs, Song song) {
        goToMusicPlayerActivity(songs, song);
    }

    @Override
    public void onArtistDetailExit() {
        closeFragment();
    }

    @Override
    public void onSongSelected(ArrayList<Song> songs, Song song) {
        goToMusicPlayerActivity(songs, song);
    }

    @Override
    public void onMusicExit() {

    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();

        if(toolbarNowPlayingView != null){
            if(MusicPlayerRemote.getCurrentSong() != null){
                toolbarNowPlayingView.setVisibility(View.VISIBLE);
            }
            else{
                toolbarNowPlayingView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onServiceDisconnected() {
        super.onServiceDisconnected();

        if(toolbarNowPlayingView != null){
            toolbarNowPlayingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlayStateChanged() {
        super.onPlayStateChanged();

        if(toolbarNowPlayingView != null){
            if(MusicPlayerRemote.getCurrentSong() != null){
                toolbarNowPlayingView.setVisibility(View.VISIBLE);
            }
            else{
                toolbarNowPlayingView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.toolbar_now_playing:
                goToNowPlayingActivity();
                break;

        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_music:
                //NavigationUtils.openMusicActivity(this, true);
                break;

            case R.id.nav_video:
                NavigationUtils.openVideosActivity(this, true);
                break;

            case R.id.nav_playlist:
                NavigationUtils.openPlaylistsActivity(this, true);
                break;

            case R.id.nav_settings:
                NavigationUtils.openSettingsActivity(this, true);
                break;

            case R.id.nav_about_app:
                NavigationUtils.openAboutActivity(this, true);
                break;

        }

        if(drawer != null){
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer != null){
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                return;
            }
        }

        if (fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStackImmediate();
            return;
        }

        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(navigationView != null){
            navigationView.setCheckedItem(R.id.nav_music);
        }
    }
}
