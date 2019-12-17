package za.co.rationalthinkers.unoplayer.android.interfaces;

public interface MusicPlayerServiceEventListener {

    public void onServiceConnected();

    public void onServiceDisconnected();

    public void onQueueChanged();

    public void onPlayingMetaChanged();

    public void onPlayStateChanged();

    public void onRepeatModeChanged();

    public void onShuffleModeChanged();

    public void onMediaStoreChanged();

}
