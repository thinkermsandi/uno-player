package za.co.rationalthinkers.unoplayer.android.handler;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

import za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService;

import static android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.META_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.PLAY_STATE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService.DUCK;
import static za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService.FOCUS_CHANGE;
import static za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService.PLAY_SONG;
import static za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService.PREPARE_NEXT;
import static za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService.RELEASE_WAKELOCK;
import static za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService.RESTORE_QUEUES;
import static za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService.SET_POSITION;
import static za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService.TRACK_ENDED;
import static za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService.TRACK_WENT_TO_NEXT;
import static za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService.UNDUCK;

public class MusicPlaybackHandler extends Handler {

    @NonNull
    private WeakReference<MusicPlayerService> mService;
    private float currentDuckVolume = 1.0f;

    public MusicPlaybackHandler(MusicPlayerService service, @NonNull Looper looper) {
        super(looper);
        mService = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(@NonNull final Message msg) {
        MusicPlayerService service = mService.get();
        if (service == null) {
            return;
        }

        switch (msg.what) {
            case DUCK:
                currentDuckVolume -= .05f;
                if (currentDuckVolume > .2f) {
                    sendEmptyMessageDelayed(DUCK, 10);
                }
                else {
                    currentDuckVolume = .2f;
                }

                service.playback.setVolume(currentDuckVolume);
                break;

            case UNDUCK:
                currentDuckVolume += .03f;
                if (currentDuckVolume < 1f) {
                    sendEmptyMessageDelayed(UNDUCK, 10);
                }
                else {
                    currentDuckVolume = 1f;
                }

                service.playback.setVolume(currentDuckVolume);
                break;

            case TRACK_WENT_TO_NEXT:
                if (service.getRepeatMode() == REPEAT_MODE_NONE && service.isLastTrack()) {
                    service.pause();
                    service.seek(0);
                }
                else {
                    service.position = service.nextPosition;
                    service.prepareNextImpl();
                    service.notifyChange(META_CHANGED);
                }
                break;

            case TRACK_ENDED:
                if (service.getRepeatMode() == REPEAT_MODE_NONE && service.isLastTrack()) {
                    service.notifyChange(PLAY_STATE_CHANGED);
                    service.seek(0);
                }
                else {
                    service.playNextSong(false);
                }
                sendEmptyMessage(RELEASE_WAKELOCK);
                break;

            case RELEASE_WAKELOCK:
                service.releaseWakeLock();
                break;

            case PLAY_SONG:
                service.playSongAtImpl(msg.arg1);
                break;

            case SET_POSITION:
                service.openTrackAndPrepareNextAt(msg.arg1);
                service.notifyChange(PLAY_STATE_CHANGED);
                break;

            case PREPARE_NEXT:
                service.prepareNextImpl();
                break;

            case RESTORE_QUEUES:
                service.restoreQueuesAndPositionIfNecessary();
                break;

            case FOCUS_CHANGE:
                switch (msg.arg1) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        if (!service.isPlaying() && service.pausedByTransientLossOfFocus) {
                            service.play();
                            service.pausedByTransientLossOfFocus = false;
                        }
                        removeMessages(DUCK);
                        sendEmptyMessage(UNDUCK);
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS:
                        // Lost focus for an unbounded amount of time: stop playback and release media playback
                        service.pause();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        // Lost focus for a short time, but we have to stop
                        // playback. We don't release the media playback because playback
                        // is likely to resume
                        boolean wasPlaying = service.isPlaying();
                        service.pause();
                        service.pausedByTransientLossOfFocus = wasPlaying;
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // Lost focus for a short time, but it's ok to keep playing
                        // at an attenuated level
                        removeMessages(UNDUCK);
                        sendEmptyMessage(DUCK);
                        break;
                }
                break;
        }
    }

}
