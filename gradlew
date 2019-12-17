package za.co.rationalthinkers.unoshare.android.fragment;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import za.co.rationalthinkers.unoshare.android.R;
import za.co.rationalthinkers.unoshare.android.service.FileTransferService;
import za.co.rationalthinkers.unoshare.android.util.Utils;

public class FilesReceiverInitFragment extends Fragment {

    private OnStartReceivingFilesActionListener mListener;

    private Context context;
    private WifiManager wifiManager;
    private SharedPreferences preferences;
    private WifiScanner mWifiScanReceiver;
    private WifiScanner mNwChangesReceiver;
    private WifiTasksHandler m_wifiScanHandler;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 100;
    private String mConnectedSSID;

    public interface OnStartReceivingFilesActionListener {
        void onReceiverServiceStarted();
    }

    public FilesReceiverInitFragment() {
        // Required empty public constructor
    }

    public static FilesReceiverInitFragment newInstance(int deviceType) {
        return new FilesReceiverInitFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_files_receiver_init, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Is file transfer service running
        if(Utils.isServiceRunning(FileTransferService.class.getCanonicalName(), context.getApplicationContext())){
            //Share mode is active, stop Share service to proceed with Receiving files
            return;
        }
    }

    private void receive(){
        if (mListener != null) {
            mListener.onReceiverServiceStarted();
        }
    }

    /**
     * Entry point to start receiver mode. Makes calls to register necessary broadcast receivers to start scanning for SHAREthem Wifi Hotspot.
     *
     * @return
     */
    private boolean startSenderScan() {
        // if targetSdkVersion >= 23
        // Get Wifi Scan results method needs GPS to be ON and COARSE location permission
        if (Utils.getTargetSDKVersion(getApplicationContext()) >= 23 && !checkLocationPermission()) {
            return false;
        }

        changeReceiverControlCheckedStatus(true);
        registerAndScanForWifiResults();
        registerForNwChanges();

        return true;
    }

    /**
     * Disables and removes SHAREthem wifi configuration from Wifi Settings. Also does cleanup work to remove handlers, un-register receivers etc..
     */
    private void disableReceiverMode() {
        if (!TextUtils.isEmpty(mConnectedSSID)) {
            if (m_areOtherNWsDisabled)
                WifiUtils.removeSTWifiAndEnableOthers(wifiManager, mConnectedSSID);
            else
                WifiUtils.removeWifiNetwork(wifiManager, mConnectedSSID);
        }

        m_wifiScanHandler.removeMessages(WAIT_FOR_CONNECT_ACTION_TIMEOUT);
        m_wifiScanHandler.removeMessages(WAIT_FOR_RECONNECT_ACTION_TIMEOUT);

        unRegisterForScanResults();
        unRegisterForNwChanges();
        removeSenderFilesListingFragmentIfExists();
    }

    static class WifiTasksHandler extends Handler {
        static final int SCAN_FOR_WIFI_RESULTS = 100;
        static final int WAIT_FOR_CONNECT_ACTION_TIMEOUT = 101;
        static final int WAIT_FOR_RECONNECT_ACTION_TIMEOUT = 102;
        private WeakReference<ReceiverActivity> mActivity;

        WifiTasksHandler(ReceiverActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ReceiverActivity activity = mActivity.get();
            if (null == activity)
                return;
            switch (msg.what) {
                case SCAN_FOR_WIFI_RESULTS:
                    if (null != activity.wifiManager)
                        activity.wifiManager.startScan();
                    break;
                case WAIT_FOR_CONNECT_ACTION_TIMEOUT:
                    Log.e(TAG, "cant connect to sender's hotspot by increasing priority, try the dirty way..");
                    activity.m_areOtherNWsDisabled = WifiUtils.connectToOpenWifi(activity.wifiManager, (String) msg.obj, true);
                    Message m = obtainMessage(WAIT_FOR_RECONNECT_ACTION_TIMEOUT);
                    m.obj = msg.obj;
                    sendMessageDelayed(m, 6000);
                    br