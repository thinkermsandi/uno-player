package za.co.rationalthinkers.unoplayer.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.annotation.NonNull;

public class NoisyAudioReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        String action = intent.getAction();
        if (action != null) {

            switch (action) {
                case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                    //TODO: Pause player when audio is becoming noisy
                    //pause
                    break;
            }

        }
    }

}
