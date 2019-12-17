package za.co.rationalthinkers.unoplayer.android.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.view.KeyEvent;

import za.co.rationalthinkers.unoplayer.android.config.Constants;
import za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_PAUSE;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_PLAY;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_REWIND;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_SKIP;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_STOP;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_TOGGLE_PAUSE;

public class MediaButtonIntentReceiver extends BroadcastReceiver {

    private static final int MSG_HEADSET_DOUBLE_CLICK_TIMEOUT = 2;
    private static final int DOUBLE_CLICK = 400;


    private static WakeLock mWakeLock = null;
    private static int mClickCounter = 0;
    private static long mLastClickTime = 0;

    @SuppressLint("HandlerLeak") // false alarm, handler is already static
    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_HEADSET_DOUBLE_CLICK_TIMEOUT:
                    int clickCount = msg.arg1;
                    String command;

                    switch (clickCount) {
                        case 1:
                            command = ACTION_TOGGLE_PAUSE;
                            break;

                        case 2:
                            command = ACTION_SKIP;
                            break;

                        case 3:
                            command = ACTION_REWIND;
                            break;

                        default:
                            command = null;
                            break;
                    }

                    if (command != null) {
                        Context context = (Context) msg.obj;
                        startService(context, command);
                    }
                    break;
            }

            releaseWakeLockIfHandlerIdle();
        }
    };

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (handleIntent(context, intent) && isOrderedBroadcast()) {
            abortBroadcast();
        }
    }

    private boolean handleIntent(Context context, Intent intent){
        String intentAction = intent.getAction();

        if(TextUtils.equals(intentAction, Intent.ACTION_MEDIA_BUTTON)){

            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                return false;
            }

            int keycode = event.getKeyCode();
            int action = event.getAction();
            long eventtime = event.getEventTime() != 0L ? event.getEventTime() : System.currentTimeMillis();

            String command = null;
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    command = ACTION_STOP;
                    break;

                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    command = ACTION_TOGGLE_PAUSE;
                    break;

                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    command = ACTION_SKIP;
                    break;

                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    command = ACTION_REWIND;
                    break;

                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    command = ACTION_PAUSE;
                    break;

                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    command = ACTION_PLAY;
                    break;
            }

            if (command != null) {
                if (action == KeyEvent.ACTION_DOWN) {
                    if(event.getRepeatCount() == 0){
                        // Only consider the first event in a sequence, not the repeat events,
                        // so that we don't trigger in cases where the first event went to
                        // a different app (e.g. when the user ends a phone call by
                        // long pressing the headset button)


                        // The service may or may not be running, but we need to send it a command.
                        if (keycode == KeyEvent.KEYCODE_HEADSETHOOK || keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                            if (eventtime - mLastClickTime >= DOUBLE_CLICK) {
                                mClickCounter = 0;
                            }

                            mClickCounter++;
                            mHandler.removeMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT);

                            Message msg = mHandler.obtainMessage(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT, mClickCounter, 0, context);

                            long delay = mClickCounter < 3 ? DOUBLE_CLICK : 0;
                            if (mClickCounter >= 3) {
                                mClickCounter = 0;
                            }
                            mLastClickTime = eventtime;
                            acquireWakeLockAndSendMessage(context, msg, delay);
                        }
                        else {
                            startService(context, command);
                        }

                        return true;

                    }

                }
            }

        }

        return false;
    }

    private static void startService(Context context, String command) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.setAction(command);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }
        else {
            context.startService(intent);
        }
    }

    private static void acquireWakeLockAndSendMessage(Context context, Message msg, long delay) {
        if (mWakeLock == null) {
            Context appContext = context.getApplicationContext();
            PowerManager pm = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.TAG_WAKELOCK_HEADSET_BUTTON);
            mWakeLock.setReferenceCounted(false);
        }

        // Make sure we don't indefinitely hold the wake lock under any circumstances
        mWakeLock.acquire(10000);

        mHandler.sendMessageDelayed(msg, delay);
    }

    private static void releaseWakeLockIfHandlerIdle() {
        if (mHandler.hasMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT)) {
            return;
        }

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

}
