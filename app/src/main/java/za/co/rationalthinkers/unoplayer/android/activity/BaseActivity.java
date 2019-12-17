package za.co.rationalthinkers.unoplayer.android.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.interfaces.MusicPlayerServiceEventListener;
import za.co.rationalthinkers.unoplayer.android.receiver.MusicPlayerStateReceiver;
import za.co.rationalthinkers.unoplayer.android.util.MusicPlayerRemote;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_NOW_PLAYING;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.MEDIA_STORE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.META_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.PLAY_STATE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.QUEUE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.REPEAT_MODE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.SHUFFLE_MODE_CHANGED;

public class BaseActivity extends AppCompatActivity implements ServiceConnection,
        MusicPlayerServiceEventListener {

    private MusicPlayerRemote.ServiceToken serviceToken = null;
    private MusicPlayerStateReceiver musicPlayerStateReceiver = null;
    private boolean receiverRegistered = false;

    private ArrayList<MusicPlayerServiceEventListener> mMusicServiceEventListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serviceToken = MusicPlayerRemote.bindToService(this, this);
        mMusicServiceEventListeners = new ArrayList<>();
    }

    public void addMusicServiceEventListener(MusicPlayerServiceEventListener listener) {
        if (listener != null) {
            mMusicServiceEventListeners.add(listener);
        }
    }

    public void removeMusicServiceEventListener(MusicPlayerServiceEventListener listener) {
        if (listener != null) {
            mMusicServiceEventListeners.remove(listener);
        }
    }

    public void goToNowPlayingActivity(){
        Intent musicPlayerIntent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
        musicPlayerIntent.setAction(ACTION_NOW_PLAYING);
        startActivity(musicPlayerIntent);
        overridePendingTransition(R.animator.slide_up, R.animator.none);
    }

    @Override
    public void onServiceConnected() {
        if (!receiverRegistered) {
            musicPlayerStateReceiver = new MusicPlayerStateReceiver(this);

            final IntentFilter filter = new IntentFilter();
            filter.addAction(META_CHANGED); // Track changes
            filter.addAction(QUEUE_CHANGED); // If a playlist has changed, notify us
            filter.addAction(PLAY_STATE_CHANGED); // Play and pause changes
            filter.addAction(REPEAT_MODE_CHANGED);
            filter.addAction(SHUFFLE_MODE_CHANGED);
            filter.addAction(MEDIA_STORE_CHANGED);

            registerReceiver(musicPlayerStateReceiver, filter);

            receiverRegistered = true;
        }

        for (MusicPlayerServiceEventListener listener : mMusicServiceEventListeners) {
            listener.onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected() {
        if (receiverRegistered) {
            unregisterReceiver(musicPlayerStateReceiver);
            receiverRegistered = false;
        }

        for (MusicPlayerServiceEventListener listener : mMusicServiceEventListeners) {
            listener.onServiceDisconnected();
        }
    }

    @Override
    public void onQueueChanged() {
        for (MusicPlayerServiceEventListener listener : mMusicServiceEventListeners) {
            listener.onQueueChanged();
        }
    }

    @Override
    public void onPlayingMetaChanged() {
        for (MusicPlayerServiceEventListener listener : mMusicServiceEventListeners) {
            listener.onPlayingMetaChanged();
        }
    }

    @Override
    public void onPlayStateChanged() {
        for (MusicPlayerServiceEventListener listener : mMusicServiceEventListeners) {
            listener.onPlayStateChanged();
        }
    }

    @Override
    public void onRepeatModeChanged() {
        for (MusicPlayerServiceEventListener listener : mMusicServiceEventListeners) {
            listener.onRepeatModeChanged();
        }
    }

    @Override
    public void onShuffleModeChanged() {
        for (MusicPlayerServiceEventListener listener : mMusicServiceEventListeners) {
            listener.onShuffleModeChanged();
        }
    }

    @Override
    public void onMediaStoreChanged() {
        for (MusicPlayerServiceEventListener listener : mMusicServiceEventListeners) {
            listener.onMediaStoreChanged();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        onServiceConnected();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        onServiceDisconnected();
    }

    @Override
    protected void onPause() {
        if(serviceToken != null){
            MusicPlayerRemote.unbindFromService(serviceToken);
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        //For Android 8.0+: service may get destroyed if in background too long
        if(MusicPlayerRemote.mService != null){
            serviceToken = MusicPlayerRemote.bindToService(this, this);
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(serviceToken != null){
            MusicPlayerRemote.unbindFromService(serviceToken);
        }

        if (receiverRegistered) {
            unregisterReceiver(musicPlayerStateReceiver);
            receiverRegistered = false;
        }

        mMusicServiceEventListeners.clear();
    }

}
