package za.co.rationalthinkers.unoplayer.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.config.Constants;
import za.co.rationalthinkers.unoplayer.android.fragment.AddPlaylistDialogFragment;
import za.co.rationalthinkers.unoplayer.android.fragment.PlaylistDetailFragment;
import za.co.rationalthinkers.unoplayer.android.fragment.PlaylistsFragment;
import za.co.rationalthinkers.unoplayer.android.loader.PlaylistLoader;
import za.co.rationalthinkers.unoplayer.android.model.Playlist;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.util.MusicPlayerRemote;
import za.co.rationalthinkers.unoplayer.android.util.NavigationUtils;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.TAG_FRAGMENT_PLAYLISTS;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.TAG_FRAGMENT_PLAYLIST_ADD;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.TAG_FRAGMENT_PLAYLIST_DETAILS;

public class PlaylistActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ImageView.OnClickListener,
        PlaylistsFragment.OnPlaylistsActionListener,
        PlaylistDetailFragment.OnPlaylistDetailActionListener,
        AddPlaylistDialogFragment.OnAddPlaylistActionListener {

    private Context context;
    private FragmentManager fragmentManager;

    //UI References
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private TextView toolbarTitleView;
    private ImageView toolbarNowPlayingView;
    private ImageView toolbarAddPlaylistView;
    private ImageView toolbarSearchView;
    private ImageView toolbarOptionsView;
    private FrameLayout fragmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        context = getApplicationContext();
        fragmentManager = getSupportFragmentManager();

        initUI();
        setUpActionBar();
        setUpNavigationDrawer();
        setUpListeners();

        goToPlaylistsFragment();
    }

    private void initUI(){
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleView = findViewById(R.id.toolbar_title);
        toolbarNowPlayingView = findViewById(R.id.toolbar_now_playing);
        toolbarAddPlaylistView = findViewById(R.id.toolbar_add_playlist);
        toolbarSearchView = findViewById(R.id.toolbar_search);
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
        toolbarAddPlaylistView.setOnClickListener(this);
    }

    private void addPlaylist(){
        DialogFragment addPlaylistDialog = AddPlaylistDialogFragment.newInstance();
        addPlaylistDialog.show(getSupportFragmentManager(), TAG_FRAGMENT_PLAYLIST_ADD);
    }

    private void goToPlaylistsFragment(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.playlists_fragment, PlaylistsFragment.newInstance());
        fragmentTransaction.commit();
    }

    private void goToPlaylistDetailsFragment(Playlist playlist){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.playlists_fragment, PlaylistDetailFragment.newInstance(playlist));
        fragmentTransaction.addToBackStack(TAG_FRAGMENT_PLAYLIST_DETAILS);
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
        goToPlaylistsFragment();
    }

    @Override
    public void onPlaylistSelected(Playlist playlist) {
        goToPlaylistDetailsFragment(playlist);
    }

    @Override
    public void onPlaylistSongSelected(ArrayList<Song> songs, Song song) {
        goToMusicPlayerActivity(songs, song);
    }

    @Override
    public void onPlaylistDetailExit() {
        closeFragment();
    }

    @Override
    public void onAddPlaylistSelected(Playlist playlist) {
        if(PlaylistLoader.addPlaylist(context, playlist) != null){
            Toast.makeText(context, playlist.getName() + " created successfully", Toast.LENGTH_SHORT).show();

            PlaylistsFragment fragment = (PlaylistsFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_PLAYLISTS);
            if(fragment != null){
                fragment.onPlaylistAdded(playlist);
            }
        }
        else{
            Toast.makeText(context, "Error! Playlist not created", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddPlaylistCancelled() {

    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();

        if(toolbarNowPlayingView != null){
            if(MusicPlayerRemote.isPlaying()){
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
            if(MusicPlayerRemote.isPlaying()){
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

            case R.id.toolbar_add_playlist:
                addPlaylist();
                break;

        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_music:
                NavigationUtils.openMusicActivity(this, true);
                break;

            case R.id.nav_video:
                NavigationUtils.openVideosActivity(this, true);
                break;

            case R.id.nav_playlist:
                //NavigationUtils.openPlaylistsActivity(this, true);
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
            navigationView.setCheckedItem(R.id.nav_playlist);
        }
    }

}
