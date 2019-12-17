package za.co.rationalthinkers.unoplayer.android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.config.Constants;
import za.co.rationalthinkers.unoplayer.android.fragment.AudioPlayerFragment;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.util.MusicPlayerRemote;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_NOW_PLAYING;

public class MusicPlayerActivity extends BaseActivity
        implements AudioPlayerFragment.OnAudioPlayerActionListener {

    private Context context;
    private FragmentManager fragmentManager;
    private ArrayList<Song> songs;

    //UI References
    private FrameLayout fragmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        context = getApplicationContext();
        fragmentManager = getSupportFragmentManager();
        songs = new ArrayList<>();

        initUI();
        setUpListeners();
    }

    private void initUI(){
        fragmentView = findViewById(R.id.music_player_fragment);
    }

    private void setUpListeners(){

    }

    private void handleIntent(@Nullable Intent intent) {
        if (intent == null) {
            Toast.makeText(context, "Playback error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if(intent.getAction() != null && intent.getAction().equals(ACTION_NOW_PLAYING)){
            goToNowPlayingFragment();
            return;
        }
        else if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)){
            Uri uri = intent.getData();

            if (uri != null) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayerRemote.playFromUri(uri);
                    }
                }, 350);

                goToNowPlayingFragment();
                return;
            }
        }
        else if(intent.getParcelableExtra(Constants.ARG_SONG) != null){
            Song song = intent.getParcelableExtra(Constants.ARG_SONG);
            songs = intent.getParcelableArrayListExtra(Constants.ARG_SONGS);

            if(songs == null || songs.size() == 0){
                songs = new ArrayList<>();
                songs.add(song); //Populate songs
            }

            int songPosition = getSongPositionInList(songs, song); //The position of the song in the list of songs

            //Start playing the songs
            MusicPlayerRemote.openQueue(songs, songPosition, true);

            goToNowPlayingFragment();
            return;
        }

        //There was an error. we need to exit the activity
        //It is because of either:
        //There were no songs supplied as arguments
        Toast.makeText(context, "Playback error", Toast.LENGTH_SHORT).show();
        finish();
    }

    private int getSongPositionInList(ArrayList<Song> songs, Song song){
        int defaultPosition = 0;

        for(int i = 0; i < songs.size(); i++){
            Song current = songs.get(i);

            if(TextUtils.equals(song.getId(), current.getId())){
                return i;
            }
        }

        return defaultPosition;
    }

    private void goToNowPlayingFragment(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.music_player_fragment, AudioPlayerFragment.newInstance());
        fragmentTransaction.commit();
    }

    private void goToPlaylistFragment(){
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.add(R.id.videos_fragment, VideoPlayerFragment.newInstance());
        //fragmentTransaction.addToBackStack(TAG_FRAGMENT_VIDEO_PLAYER);
        //fragmentTransaction.commit();
    }

    @Override
    public void onAudioPlayerExit() {
        finish();
    }

    @Override
    public void onViewPlaylist() {
        goToPlaylistFragment();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();

        handleIntent(getIntent());
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStackImmediate();
            return;
        }

        super.onBackPressed();
        overridePendingTransition(R.animator.none, R.animator.slide_down);
    }
}
