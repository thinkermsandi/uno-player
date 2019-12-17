package za.co.rationalthinkers.unoplayer.android.runnable;

import android.os.Handler;

import java.lang.ref.WeakReference;

import za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.PLAY_STATE_CHANGED;

public class ThrottledSeekHandler implements Runnable {

    // milliseconds to throttle before calling run() to aggregate events
    private WeakReference<MusicPlayerService> mService;
    private static final long THROTTLE = 500;
    private Handler mHandler;

    public ThrottledSeekHandler(MusicPlayerService service, Handler handler) {
        mHandler = handler;
        mService = new WeakReference<>(service);
    }

    public void notifySeek() {
        mHandler.removeCallbacks(this);
        mHandler.postDelayed(this, THROTTLE);
    }

    @Override
    public void run() {
        MusicPlayerService service = mService.get();
        if (service == null) {
            return;
        }

        //service.savePositionInTrack();
        service.sendPublicIntent(PLAY_STATE_CHANGED); // for musixmatch synced lyrics
    }

}
