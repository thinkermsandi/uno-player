package za.co.rationalthinkers.unoplayer.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.config.Constants;
import za.co.rationalthinkers.unoplayer.android.fragment.VideoPlayerFragment;
import za.co.rationalthinkers.unoplayer.android.model.Video;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.ARG_VIDEO;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.TAG_FRAGMENT_VIDEO_PLAYER;

public class VideoPlayerActivity extends BaseActivity
        implements VideoPlayerFragment.OnVideoPlayerActionListener {

    private Context context;
    private FragmentManager fragmentManager;
    private ArrayList<Video> videos;

    //UI References
    private FrameLayout fragmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);

        context = getApplicationContext();
        fragmentManager = getSupportFragmentManager();
        videos = new ArrayList<>();

        initUI();
        setUpListeners();

        handleIntent(getIntent());
    }

    private void initUI(){
        fragmentView = findViewById(R.id.video_player_fragment);
    }

    private void setUpListeners(){

    }

    private void handleIntent(@Nullable Intent intent) {
        if (intent == null) {
            Toast.makeText(context, "Playback error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)){
            Uri uri = intent.getData();

            if (uri != null) {
                Video video = new Video();
                video.setPath(uri.getPath());

                goToNowPlayingFragment(video);
                return;
            }
        }
        else if(intent.getParcelableExtra(Constants.ARG_VIDEO) != null){
            Video video = intent.getParcelableExtra(ARG_VIDEO);

            goToNowPlayingFragment(video);
            return;
        }

        //There was an error. we need to exit the activity
        //It is because of either:
        //There were no videos supplied as arguments
        Toast.makeText(context, "Playback error", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void goToNowPlayingFragment(Video video){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.video_player_fragment, VideoPlayerFragment.newInstance(video));
        fragmentTransaction.commit();
    }

    private void goToPlaylistFragment(){
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.add(R.id.videos_fragment, VideoPlayerFragment.newInstance());
        //fragmentTransaction.addToBackStack(TAG_FRAGMENT_VIDEO_PLAYER);
        //fragmentTransaction.commit();
    }


    @Override
    public void onViewPlaylist() {
        goToPlaylistFragment();
    }

    @Override
    public void onRotateScreen() {
        int orientation = getRequestedOrientation();

        switch(orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;

            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }

    @Override
    public void onVideoPlayerExit() {
        //TODO: Go to the playlist fragment if playlist has finished playing
        finish();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();

        //TODO: Show now playing icon if a song is playing
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStackImmediate();
            return;
        }

        super.onBackPressed();
    }
}
