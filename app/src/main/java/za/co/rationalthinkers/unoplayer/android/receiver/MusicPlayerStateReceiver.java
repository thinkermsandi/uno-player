package za.co.rationalthinkers.unoplayer.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

import za.co.rationalthinkers.unoplayer.android.activity.BaseActivity;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.MEDIA_STORE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.META_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.PLAY_STATE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.QUEUE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.REPEAT_MODE_CHANGED;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.SHUFFLE_MODE_CHANGED;

public class MusicPlayerStateReceiver extends BroadcastReceiver {

    private WeakReference<BaseActivity> _activity;

    public MusicPlayerStateReceiver(BaseActivity activity) {
        _activity = new WeakReference<>(activity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        BaseActivity activity = _activity.get();
        if (activity == null) {
            return;
        }

        String action = intent.getAction();
        if (action != null) {

            if(TextUtils.equals(action, META_CHANGED)){
                activity.onPlayingMetaChanged();
            }
            else if(TextUtils.equals(action, QUEUE_CHANGED)){
                activity.onQueueChanged();
            }
            else if(TextUtils.equals(action, PLAY_STATE_CHANGED)){
                activity.onPlayStateChanged();
            }
            else if(TextUtils.equals(action, REPEAT_MODE_CHANGED)){
                activity.onRepeatModeChanged();
            }
            else if(TextUtils.equals(action, SHUFFLE_MODE_CHANGED)){
                activity.onShuffleModeChanged();
            }
            else if(TextUtils.equals(action, MEDIA_STORE_CHANGED)){
                activity.onMediaStoreChanged();
            }

        }

    }

}
