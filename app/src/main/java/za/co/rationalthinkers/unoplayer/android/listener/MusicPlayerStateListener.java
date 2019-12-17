package za.co.rationalthinkers.unoplayer.android.listener;

/**
 * Listens for playback changes to send the the fragments bound to this activity
 */
public interface MusicPlayerStateListener {

    void onServiceConnected();

    void onServiceDisconnected();

    /**
     * Called when QUEUE_CHANGED is invoked
     */
    void onQueueChanged();

    /**
     * Called when META_CHANGED is invoked
     */
    void onMetaChanged();

    void onPlayStateChanged();

    void onRepeatModeChanged();

    void onShuffleModeChanged();

    void onMediaStoreChanged();

    /**
     * Called when PLAYLIST_CHANGED is invoked
     */
    void onPlaylistChanged();

}
