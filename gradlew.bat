package za.co.rationalthinkers.unoshare.android.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import za.co.rationalthinkers.unoshare.android.R;
import za.co.rationalthinkers.unoshare.android.util.HotspotUtils;

public class FilesSenderInitFragment extends Fragment {

    private OnStartSendingFilesActionListener mListener;

    private Context context;
    private HotspotUtils hotspotControl;
    private boolean isApEnabled = false;
    private boolean shouldAutoConnect = true;

    public interface OnStartSendingFilesActionListener {
        void onSenderServiceStarted();
    }

    public FilesSenderInitFragment() {
        // Required empty public constructor
    }

    public static FilesSenderInitFragment newInstance(int deviceType) {
        return new FilesSenderInitFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        hotspotControl = HotspotUtils.getInstance(context.getApplicationContext());

        m_p2pServerUpdatesListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isFinishing() || null == intent)
                    return;
                int intentType = intent.getIntExtra(SHAREthemService.ShareIntents.TYPE, 0);
                if (intentType == SHAREthemService.ShareIntents.Types.FILE_TRANSFER_STATUS) {
                    String fileName = intent.getStringExtra(SHAREthemService.ShareIntents.SHARE_SERVER_UPDATE_FILE_NAME);
                    updateReceiverListItem(intent.getStringExtra(SHAREthemService.ShareIntents.SHARE_CLIENT_IP), intent.getIntExtra(SHAREthemService.ShareIntents.SHARE_TRANSFER_PROGRESS, -1), intent.getStringExtra(SHAREthemService.ShareIntents.SHARE_SERVER_UPDATE_TEXT), fileName);
                } els