package za.co.rationalthinkers.unoplayer.android.observer;

import android.database.ContentObserver;
import android.os.Handler;

import java.lang.ref.WeakReference;

import za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.MEDIA_STORE_CHANGED;

public class MusicFilesObserver extends ContentObserver implements Runnable {

    // milliseconds to delay before calling refresh to aggregate events
    private WeakReference<MusicPlayerService> mService;
    private static final long REFRESH_DELAY = 500;
    private Handler mHandler;

    public MusicFilesObserver(MusicPlayerService service, Handler handler) {
        super(handler);
        mHandler = handler;
        mService = new WeakReference<>(service);
    }

    @Override
    public void onChange(boolean selfChange) {
        // if a change is detected, remove any scheduled callback
        // then post a new one. This is intended to prevent closely
        // spaced events from generating multiple refresh calls
        mHandler.removeCallbacks(this);
        mHandler.postDelayed(this, REFRESH_DELAY);
    }

    @Override
    public void run() {
        MusicPlayerService service = mService.get();
        if (service == null) {
            return;
        }

        // actually call refresh when the delayed callback fires
        // do not send a sticky broadcast here
        service.handleAndSendChangeInternal(MEDIA_STORE_CHANGED);
    }

}
