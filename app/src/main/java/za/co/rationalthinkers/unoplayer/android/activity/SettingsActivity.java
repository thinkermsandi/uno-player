package za.co.rationalthinkers.unoplayer.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.fragment.SettingsFragment;
import za.co.rationalthinkers.unoplayer.android.util.MusicPlayerRemote;
import za.co.rationalthinkers.unoplayer.android.util.NavigationUtils;

public class SettingsActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ImageView.OnClickListener,
        SettingsFragment.OnSettingsActionListener {

    private Context context;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

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
        setContentView(R.layout.activity_settings);

        context = getApplicationContext();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        initUI();
        setUpActionBar();
        setUpNavigationDrawer();
        setUpListeners();

        setUpDefaultFragment();
    }

    private void initUI(){
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbarSearchView = findViewById(R.id.toolbar_search);
        toolbarTitleView = findViewById(R.id.toolbar_title);
        toolbarNowPlayingView = findViewById(R.id.toolbar_now_playing);
        toolbarOptionsView = findViewById(R.id.toolbar_options);
        fragmentView = findViewById(R.id.settings_fragment);
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

    private void setUpDefaultFragment(){
        fragmentTransaction.add(R.id.settings_fragment, SettingsFragment.newInstance());
        fragmentTransaction.commit();
    }

    @Override
    public void onNotificationsSettingsSelected() {

    }

    @Override
    public void onOtherSettingsSelected() {

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
                NavigationUtils.openPlaylistsActivity(this, true);
                break;

            case R.id.nav_settings:
                //NavigationUtils.openSettingsActivity(this, true);
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
            navigationView.setCheckedItem(R.id.nav_settings);
        }
    }

}
