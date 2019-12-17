package za.co.rationalthinkers.unoplayer.android.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import za.co.rationalthinkers.unoplayer.android.BuildConfig;
import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.util.MusicPlayerRemote;
import za.co.rationalthinkers.unoplayer.android.util.NavigationUtils;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_NOW_PLAYING;

public class AboutActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ImageView.OnClickListener {

    private Context context;

    //UI References
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private ImageView toolbarSearchView;
    private TextView toolbarTitleView;
    private ImageView toolbarNowPlayingView;
    private ImageView toolbarOptionsView;
    private LinearLayout aboutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        context = getApplicationContext();

        initUI();
        setUpActionBar();
        setUpNavigationDrawer();
        setUpListeners();

        setUpAppInfo();
    }

    private void initUI(){
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbarSearchView = findViewById(R.id.toolbar_search);
        toolbarTitleView = findViewById(R.id.toolbar_title);
        toolbarNowPlayingView = findViewById(R.id.toolbar_now_playing);
        toolbarOptionsView = findViewById(R.id.toolbar_options);
        aboutLayout = findViewById(R.id.about_layout);
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

    private void setUpAppInfo(){
        String version = BuildConfig.VERSION_NAME;
        String appId = BuildConfig.APPLICATION_ID;

        Element versionElement = new Element();
        versionElement.setTitle("Version " + version);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("The only Media Player you'll ever need")
                .addItem(versionElement)
                .addGroup("Connect with us")
                .addPlayStore(appId)
                .create();

        aboutLayout.addView(aboutPage);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
                NavigationUtils.openSettingsActivity(this, true);
                break;

            case R.id.nav_about_app:
                //NavigationUtils.openAboutActivity(this, true);
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

        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(navigationView != null){
            navigationView.setCheckedItem(R.id.nav_about_app);
        }
    }

}
