package za.co.rationalthinkers.unoplayer.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import za.co.rationalthinkers.unoplayer.android.config.HeadsetMode;

public class HeadsetStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {

            switch (action) {
                case Intent.ACTION_HEADSET_PLUG:
                    int state = intent.getIntExtra("state", -1);

                    switch (state) {
                        case HeadsetMode.HEADSET_UNPLUGGED:
                            //TODO: Pause player when headset is unplugged
                            //pause();
                            break;
                        case HeadsetMode.HEADSET_PLUGGED:
                            //TODO: Resume player when headset is plugged
                            //play();
                            break;
                    }

                    break;
            }

        }
    }

}
