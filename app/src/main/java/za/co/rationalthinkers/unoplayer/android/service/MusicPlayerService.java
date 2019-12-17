package za.co.rationalthinkers.unoplayer.android.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import za.co.rationalthinkers.unoplayer.android.config.Constants;
import za.co.rationalthinkers.unoplayer.android.config.RepeatMode;
import za.co.rationalthinkers.unoplayer.android.config.Settings;
import za.co.rationalthinkers.unoplayer.android.config.ShuffleMode;
import za.co.rationalthinkers.unoplayer.android.handler.MusicPlaybackHandler;
import za.co.rationalthinkers.unoplayer.android.listener.MusicPlayerPhoneStateListener;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.notification.MusicPlayerNotification;
import za.co.rationalthinkers.unoplayer.android.observer.MusicFilesObserver;
import za.co.rationalthinkers.unoplayer.android.receiver.HeadsetStateReceiver;
import za.co.rationalthinkers.unoplayer.android.receiver.MediaButtonIntentReceiver;
import za.co.rationalthinkers.unoplayer.android.receiver.NoisyAudioReceiver;
import za.co.rationalthinkers.unoplayer.android.runnable.ThrottledSeekHandler;
import za.co.rationalthinkers.unoplayer.android.util.MediaPlayerUtils;
import za.co.rationalthinkers.unoplayer.android.util.MusicPlayer;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_PAUSE;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_PLAY;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_PLAY_PLAYLIST;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_QUIT;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_REWIND;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_SKIP;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_STOP;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_TOGGLE_PAUSE;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.META_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.MUSIC_PACKAGE_NAME;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.PACKAGE_NAME;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.PLAY_STATE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.QUEUE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.REPEAT_MODE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.SHUFFLE_MODE_CHANGED;

public class MusicPlayerService extends Service
        implements MediaPlayerUtils.PlaybackCallbacks {

    public static final int RELEASE_WAKELOCK = 0;
    public static final int TRACK_ENDED = 1;
    public static final int TRACK_WENT_TO_NEXT = 2;
    public static final int PLAY_SONG = 3;
    public static final int PREPARE_NEXT = 4;
    public static final int SET_POSITION = 5;
    public static final int RESTORE_QUEUES = 9;
    public static final int FOCUS_CHANGE = 6;
    public static final int DUCK = 7;
    public static final int UNDUCK = 8;
    public static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    private Context context;
    private Settings settings;
    private PowerManager.WakeLock wakeLock;
    private ArrayList<Song> playlist = new ArrayList<>();
    private ArrayList<Song> originalPlaylist = new ArrayList<>();

    private AudioManager audioManager;
    private MusicPlayer musicPlayer;
    public MediaPlayerUtils playback;
    private MediaSessionCompat mediaSession;
    private MusicPlayerPhoneStateListener phoneStateListener;
    private MusicPlayerNotification playingNotification;
    private ContentObserver musicFilesObserver;

    private boolean isServiceBound;
    private int repeatMode;
    private int shuffleMode;
    public int position = -1;
    public int nextPosition = -1;

    public boolean queuesRestored;
    public boolean pausedByTransientLossOfFocus;
    public boolean notHandledMetaChangedForCurrentTrack;
    public boolean headsetReceiverRegistered = false;
    public boolean becomingNoisyReceiverRegistered;

    private Handler uiThreadHandler;
    private HandlerThread musicPlayerHandlerThread;
    private MusicPlaybackHandler musicPlaybackHandler;
    private ThrottledSeekHandler throttledSeekHandler;

    private NoisyAudioReceiver becomingNoisyReceiver;
    private HeadsetStateReceiver headsetStateReceiver;

    private IntentFilter becomingNoisyReceiverIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private AudioManager.OnAudioFocusChangeListener audioFocusListener;
    private final IBinder musicBind = new MusicBinder();

    public MusicPlayerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        isServiceBound = true;
        return musicBind;
    }

    @Override
    public void onRebind(Intent intent) {
        isServiceBound = true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isServiceBound = false;
        if (!isPlaying()) {
            stopSelf();
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        settings = new Settings(context);
        becomingNoisyReceiver = new NoisyAudioReceiver();
        headsetStateReceiver = new HeadsetStateReceiver();
        audioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(final int focusChange) {
                musicPlaybackHandler.obtainMessage(FOCUS_CHANGE, focusChange, 0).sendToTarget();
            }
        };

        setUpPhoneStateListener();
        setUpWakeLock();
        setUpMusicPlayerHandler();
        setUpMediaPlayer();
        setUpMediaSession();
        setUpPlayingNotification();

        uiThreadHandler = new Handler();

        setUpMusicFilesObserver();

        restoreState();
        registerHeadsetEvents();

        sendBroadcast(new Intent("za.co.rationalthinkers.unoplayer.UNO_PLAYER_SERVICE_CREATED"));
    }

    private void setUpPhoneStateListener(){
        phoneStateListener = new MusicPlayerPhoneStateListener(this);

        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, MusicPlayerPhoneStateListener.LISTEN_NONE);
        }
    }

    private void setUpWakeLock(){
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        }
        wakeLock.setReferenceCounted(false);
    }

    private void setUpMusicPlayerHandler(){
        musicPlayerHandlerThread = new HandlerThread("PlaybackHandler");
        musicPlayerHandlerThread.start();
        musicPlaybackHandler = new MusicPlaybackHandler(this, musicPlayerHandlerThread.getLooper());
    }

    private void setUpMediaPlayer(){
        playback = new MediaPlayerUtils(this);
        playback.setCallbacks(this);
    }

    private void setUpMusicFilesObserver(){
        musicFilesObserver = new MusicFilesObserver(this, musicPlaybackHandler);
        throttledSeekHandler = new ThrottledSeekHandler(this, musicPlaybackHandler);
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, true, musicFilesObserver);
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, musicFilesObserver);
    }

    private void setUpMediaSession() {
        ComponentName mediaButtonReceiverComponentName = new ComponentName(getApplicationContext(), MediaButtonIntentReceiver.class);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mediaButtonReceiverComponentName);

        PendingIntent mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);

        mediaSession = new MediaSessionCompat(this, Constants.TAG_MEDIA_SESSION, mediaButtonReceiverComponentName, mediaButtonReceiverPendingIntent);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onPause() {
                pause();
            }

            @Override
            public void onSkipToNext() {
                playNextSong(true);
            }

            @Override
            public void onSkipToPrevious() {
                back(true);
            }

            @Override
            public void onStop() {
                quit();
            }

            @Override
            public void onSeekTo(long pos) {
                seek((int) pos);
            }
        });

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSession.setMediaButtonReceiver(mediaButtonReceiverPendingIntent);

        mediaSession.setActive(true);
    }

    private void setUpPlayingNotification(){
        playingNotification = new MusicPlayerNotification(this);
    }

    private void restoreState() {
        shuffleMode = settings.getShuffleMode();
        repeatMode = settings.getRepeatMode();
        handleAndSendChangeInternal(SHUFFLE_MODE_CHANGED);
        handleAndSendChangeInternal(REPEAT_MODE_CHANGED);

        musicPlaybackHandler.removeMessages(RESTORE_QUEUES);
        musicPlaybackHandler.sendEmptyMessage(RESTORE_QUEUES);
    }

    private void registerHeadsetEvents() {
        if (!headsetReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(headsetStateReceiver, intentFilter);
            headsetReceiverRegistered = true;
        }
    }

    public void prepareNext() {
        musicPlaybackHandler.removeMessages(PREPARE_NEXT);
        musicPlaybackHandler.obtainMessage(PREPARE_NEXT).sendToTarget();
    }

    public boolean prepareNextImpl() {
        synchronized (this) {
            try {
                int nextPosition = getNextPosition(false);

                Song song = getSongAt(nextPosition);
                playback.setNextDataSource(song.getPath());
                this.nextPosition = nextPosition;
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
    }

    public void play() {
        synchronized (this) {
            if (requestFocus()) {
                if (!playback.isPlaying()) {
                    //Log.e("servix", "if playback.isPlaying()");
                    if (!playback.isInitialized()) {
                        playSongAt(position);
                    }
                    else {
                        playback.start();

                        if (!becomingNoisyReceiverRegistered) {
                            registerReceiver(becomingNoisyReceiver, becomingNoisyReceiverIntentFilter);
                            becomingNoisyReceiverRegistered = true;
                        }

                        if (notHandledMetaChangedForCurrentTrack) {
                            handleChangeInternal(META_CHANGED);
                            notHandledMetaChangedForCurrentTrack = false;
                        }

                        notifyChange(PLAY_STATE_CHANGED);

                        // fixes a bug where the volume would stay ducked because the AudioManager.AUDIOFOCUS_GAIN event is not sent
                        musicPlaybackHandler.removeMessages(DUCK);
                        musicPlaybackHandler.sendEmptyMessage(UNDUCK);
                    }
                }
            }
            else {
                Toast.makeText(this, "Audio focus is denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void pause() {
        pausedByTransientLossOfFocus = false;

        if (playback.isPlaying()) {
            playback.pause();
            notifyChange(PLAY_STATE_CHANGED);
        }
    }

    public int seek(int millis) {
        synchronized (this) {
            try {
                int newPosition = playback.seek(millis);
                throttledSeekHandler.notifySeek();
                return newPosition;
            }
            catch (Exception e) {
                return -1;
            }
        }
    }

    public void clearQueue() {
        playlist.clear();
        originalPlaylist.clear();

        setPosition(-1);
        notifyChange(QUEUE_CHANGED);
    }

    public void playNextSong(boolean force) {
        //Log.e("servix", "playNextSong(boolean force)");
        playSongAt(getNextPosition(force));
    }

    public void playSongAt(final int position) {
        musicPlaybackHandler.removeMessages(PLAY_SONG);
        musicPlaybackHandler.obtainMessage(PLAY_SONG, position, 0).sendToTarget();
    }

    public void playSongAtImpl(int position) {
        if (openTrackAndPrepareNextAt(position)) {
            play();
        }
        else {
            Toast.makeText(this, "Not able to play file", Toast.LENGTH_SHORT).show();
        }
    }

    public void playPreviousSong(boolean force) {
        playSongAt(getPreviousPosition(force));
    }

    public void back(boolean force) {
        if (getSongProgressMillis() > 2000) {
            seek(0);
        }
        else {
            playPreviousSong(force);
        }
    }

    public Song getCurrentSong() {
        return getSongAt(position);
    }

    public ArrayList<Song> getPlayingQueue() {
        return playlist;
    }

    public Song getSongAt(int position) {
        if (position >= 0 && position < playlist.size()) {
            //Log.e("Song servix", "Playing song at position " + position);
            return getPlayingQueue().get(position);
        }

        return null;
    }

    private String getTrackUri(@NonNull Song song) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(song.getId())).toString();
    }

    public int getNextPosition(boolean force) {
        int pos = position + 1;

        switch (getRepeatMode()) {
            case RepeatMode.REPEAT_ALL:
                if (isLastTrack()) {
                    pos = 0;
                }
                break;

            case RepeatMode.REPEAT_CURRENT:
                if (force) {
                    if (isLastTrack()) {
                        pos = 0;
                    }
                }
                else {
                    pos -= 1;
                }
                break;

            case RepeatMode.REPEAT_OFF:
                if (isLastTrack()) {
                    pos -= 1;
                }
                break;

            default:

        }

        return pos;
    }

    public int getPreviousPosition(boolean force) {
        int newPosition = position - 1;

        switch (repeatMode) {
            case RepeatMode.REPEAT_ALL:
                if (newPosition < 0) {
                    newPosition = getPlayingQueue().size() - 1;
                }
                break;

            case RepeatMode.REPEAT_CURRENT:
                if (force) {
                    if (newPosition < 0) {
                        newPosition = getPlayingQueue().size() - 1;
                    }
                }
                else {
                    newPosition = position;
                }
                break;

            case RepeatMode.REPEAT_OFF:
                if (newPosition < 0) {
                    newPosition = 0;
                }
                break;

            default:

        }

        return newPosition;
    }

    public boolean isLastTrack() {
        return position == playlist.size() - 1;
    }

    public boolean openTrackAndPrepareNextAt(int position) {
        synchronized (this) {
            //Log.e("servix", "openTrackAndPrepareNextAt");
            this.position = position;
            boolean prepared = openCurrent();

            if(prepared){
                prepareNextImpl();
            }

            //Log.e("servix", "before notifyChange(META_CHANGED)");
            notifyChange(META_CHANGED);
            //Log.e("servix", "after notifyChange(META_CHANGED)");
            notHandledMetaChangedForCurrentTrack = false;

            return prepared;
        }
    }

    private boolean openCurrent() {
        synchronized (this) {
            try {
                return playback.setDataSource(getTrackUri(getCurrentSong()));
            }
            catch (Exception e) {
                return false;
            }
        }
    }

    public void acquireWakeLock(long milli) {
        wakeLock.acquire(milli);
    }

    public void releaseWakeLock() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private AudioManager getAudioManager() {
        if (audioManager == null) {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }

        return audioManager;
    }

    public void openQueue(@Nullable ArrayList<Song> playingQueue, int startPosition, boolean startPlaying) {
        //Log.e("Songs Servix", "Songs are " + playingQueue.size() + ". Position is: " + startPosition);
        if (playingQueue != null && !playingQueue.isEmpty() && startPosition >= 0 && startPosition < playingQueue.size()) {
            // it is important to copy the playing queue here first as we might add/remove playlist later
            originalPlaylist = new ArrayList<>(playingQueue);
            this.playlist = new ArrayList<>(originalPlaylist);

            int position = startPosition;

            if (startPlaying) {
                playSongAt(position);
            }
            else {
                setPosition(position);
            }

            notifyChange(QUEUE_CHANGED);
        }
        else{
            //Log.e("Songs Servix", "Bounds error");
        }
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(final int repeatMode) {
        if(settings.getRepeatMode() == repeatMode){
            return;
        }

        switch (repeatMode) {
            case RepeatMode.REPEAT_OFF:
            case RepeatMode.REPEAT_ALL:
            case RepeatMode.REPEAT_CURRENT:
                this.repeatMode = repeatMode;
                settings.setSetRepeatMode(repeatMode);
                prepareNext();
                handleAndSendChangeInternal(REPEAT_MODE_CHANGED);
                break;
        }
    }

    public int getShuffleMode() {
        return shuffleMode;
    }

    public void setShuffleMode(final int shuffleMode) {
        if(settings.getShuffleMode() == shuffleMode){
            return;
        }

        settings.setSetShuffleMode(shuffleMode);
        switch (shuffleMode) {
            case ShuffleMode.SHUFFLE_ON:
                this.shuffleMode = shuffleMode;
                //TODO: Shuffle the list
                //ShuffleHelper.INSTANCE.makeShuffleList(this.getPlayingQueue(), getPosition());
                //position = 0;
                break;

            case ShuffleMode.SHUFFLE_OFF:
                this.shuffleMode = shuffleMode;
                //TODO: Disable shuffle
                //int currentSongId = getCurrentSong().id;
                //playingQueue = new ArrayList<>(originalPlayingQueue);
                //int newPosition = 0;
                //for (Song song : getPlayingQueue()) {
                //    if (song.id == currentSongId) {
                //        newPosition = getPlayingQueue().indexOf(song);
                //    }
                //}
                //position = newPosition;
                break;
        }

        handleAndSendChangeInternal(SHUFFLE_MODE_CHANGED);
        notifyChange(QUEUE_CHANGED);
    }

    public synchronized void restoreQueuesAndPositionIfNecessary() {
        queuesRestored = true;
    }

    public boolean isPlaying() {
        return playback != null && playback.isPlaying();
    }

    public int getSongProgressMillis() {
        return playback.position();
    }

    public int getSongDurationMillis() {
        return playback.duration();
    }

    /*public long getQueueDurationMillis(int position) {
        long duration = 0;

        for (int i = position + 1; i < playingQueue.size(); i++){
            duration += playingQueue.get(i).getDuration();
        }

        return duration;
    }*/

    public int getPosition() {
        return position;
    }

    public void setPosition(final int position) {
        // handle this on the handlers thread to avoid blocking the ui thread
        musicPlaybackHandler.removeMessages(SET_POSITION);
        musicPlaybackHandler.obtainMessage(SET_POSITION, position, 0).sendToTarget();
    }

    public MediaSessionCompat.Token getMediaSessionToken(){
        return mediaSession.getSessionToken();
    }

    private boolean requestFocus() {
        return (getAudioManager().requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    private void updateMediaSessionPlaybackState() {
        int state = isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

        mediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(state, position, 1)
                        .build());
    }

    private void updateMediaSessionMetaData() {
        final Song song = getCurrentSong();

        if (song == null) {
            mediaSession.setMetadata(null);
            return;
        }

        final MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtistName())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.getArtistName())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbumTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDuration())
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, position + 1)
                .putLong(MediaMetadataCompat.METADATA_KEY_YEAR, song.getYear())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, getPlayingQueue().size());
        }

        mediaSession.setMetadata(metaData.build());
    }

    public void updateNotification() {
        if (playingNotification != null && getCurrentSong() != null) {
            playingNotification.update();
        }
    }

    public void notifyChange(@NonNull final String what) {
        handleAndSendChangeInternal(what);
        //TODO: sendPublicIntent(what);
    }

    public void handleAndSendChangeInternal(@NonNull final String what) {
        handleChangeInternal(what);
        sendChangeInternal(what);
    }

    private void sendChangeInternal(final String what) {
        sendBroadcast(new Intent(what));
    }

    public void handleChangeInternal(String what) {

        if(TextUtils.equals(what, PLAY_STATE_CHANGED)){
            updateNotification();
            updateMediaSessionPlaybackState(); // Update the lockscreen controls
            //TODO: If song has stopped while it was playing (position > 0) save the position of the song
        }
        else if(TextUtils.equals(what, META_CHANGED)){
            updateNotification();
            updateMediaSessionMetaData();
            //TODO: Save position, song position then save song to history and also increase play count
        }
        else if(TextUtils.equals(what, QUEUE_CHANGED)){
            updateMediaSessionMetaData(); // because playing queue size might have changed
            //TODO: Save the queue, position and position of the song
            if (getPlayingQueue().size() > 0) {
                prepareNext();
            }
            else {
                playingNotification.stop();
            }
        }

    }

    // to let other apps know whats playing. i.E. last.fm (scrobbling) or musixmatch
    public void sendPublicIntent(@NonNull final String what) {
        final Intent intent = new Intent(what.replace(PACKAGE_NAME, MUSIC_PACKAGE_NAME));

        final Song song = getCurrentSong();
        if(song != null){
            intent.putExtra("id", song.getId());
            intent.putExtra("artist", song.getArtistName());
            intent.putExtra("album", song.getAlbumTitle());
            intent.putExtra("track", song.getTitle());
            intent.putExtra("duration", song.getDuration());
            intent.putExtra("position", (long) getSongProgressMillis());
            intent.putExtra("playing", isPlaying());
            intent.putExtra("scrobbling_source", PACKAGE_NAME);

            sendStickyBroadcast(intent);
        }

    }

    private void releaseResources() {
        musicPlaybackHandler.removeCallbacksAndMessages(null);
        musicPlayerHandlerThread.quitSafely();

        playback.release();
        playback = null;
        mediaSession.release();
    }

    public int quit() {
        pause();
        playingNotification.stop();

        if (isServiceBound) {
            return START_STICKY;
        }
        else {
            getAudioManager().abandonAudioFocus(audioFocusListener);
            stopSelf();
            return START_NOT_STICKY;
        }
    }

    @Override
    public void onTrackWentToNext() {
        musicPlaybackHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
    }

    @Override
    public void onTrackEnded() {
        acquireWakeLock(30000);
        musicPlaybackHandler.sendEmptyMessage(TRACK_ENDED);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() != null) {
                restoreQueuesAndPositionIfNecessary();

                String action = intent.getAction();

                if(TextUtils.equals(action, ACTION_TOGGLE_PAUSE)){
                    if (isPlaying()) {
                        pause();
                    }
                    else {
                        play();
                    }
                }
                else if(TextUtils.equals(action, ACTION_PAUSE)){
                    pause();
                }
                else if(TextUtils.equals(action, ACTION_PLAY)){
                    play();
                }
                else if(TextUtils.equals(action, ACTION_PLAY_PLAYLIST)){

                }
                else if(TextUtils.equals(action, ACTION_REWIND)){
                    back(true);
                }
                else if(TextUtils.equals(action, ACTION_SKIP)){
                    playNextSong(true);
                }
                else if(TextUtils.equals(action, ACTION_STOP)){

                }
                else if(TextUtils.equals(action, ACTION_QUIT)){
                    //pending quit = false
                    quit();
                }

            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (becomingNoisyReceiverRegistered) {
            unregisterReceiver(becomingNoisyReceiver);
            becomingNoisyReceiverRegistered = false;
        }

        if (headsetReceiverRegistered) {
            unregisterReceiver(headsetStateReceiver);
            headsetReceiverRegistered = false;
        }

        mediaSession.setActive(false);

        quit();
        releaseResources();

        getContentResolver().unregisterContentObserver(musicFilesObserver);

        wakeLock.release();
    }

    public class MusicBinder extends Binder {
        @NonNull
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

}
