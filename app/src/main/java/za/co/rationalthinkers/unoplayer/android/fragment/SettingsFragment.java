package za.co.rationalthinkers.unoplayer.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import za.co.rationalthinkers.unoplayer.android.R;

public class SettingsFragment extends Fragment
        implements View.OnClickListener {

    private OnSettingsActionListener mListener;

    private Context mContext;

    //UI references
    LinearLayout mNotificationSettingsView;
    LinearLayout mOtherSettingsView;

    public interface OnSettingsActionListener {
        void onNotificationsSettingsSelected();
        void onOtherSettingsSelected();
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setUpListeners();
    }

    private void initUI(View view){
        mNotificationSettingsView = view.findViewById(R.id.notifications_container);
        mOtherSettingsView = view.findViewById(R.id.other_container);
    }

    private void setUpListeners(){
        mNotificationSettingsView.setOnClickListener(this);
        mOtherSettingsView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.notifications_container:
                if(mListener != null){
                    mListener.onNotificationsSettingsSelected();
                }
                break;

            case R.id.other_container:
                if(mListener != null){
                    mListener.onOtherSettingsSelected();
                }
                break;

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnSettingsActionListener) {
            mListener = (OnSettingsActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnSettingsActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
