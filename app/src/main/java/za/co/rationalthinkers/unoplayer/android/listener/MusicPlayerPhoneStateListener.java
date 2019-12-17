package za.co.rationalthinkers.unoplayer.android.listener;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.lang.ref.WeakReference;

import za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService;

public class MusicPlayerPhoneStateListener extends PhoneStateListener {

    private WeakReference<MusicPlayerService> mService;

    public MusicPlayerPhoneStateListener(MusicPlayerService service){
        mService = new WeakReference<>(service);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        MusicPlayerService service = mService.get();
        if (service == null) {
            return;
        }

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                service.play();
                break;

            case TelephonyManager.CALL_STATE_RINGING:

            case TelephonyManager.CALL_STATE_OFFHOOK:
                service.pause(); //A call is dialing, active or on hold
                break;

            default:
        }

        super.onCallStateChanged(state, incomingNumber);
    }

}
