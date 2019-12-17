package za.co.rationalthinkers.unoplayer.android.fragment;


import android.Manifest;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.adapter.VideosAdapter;
import za.co.rationalthinkers.unoplayer.android.callback.FilterResultCallback;
import za.co.rationalthinkers.unoplayer.android.model.Video;
import za.co.rationalthinkers.unoplayer.android.util.ModelFilterUtils;

public class VideosFragment extends Fragment
        implements VideosAdapter.OnItemClickListener {

    private OnVideosActionListener mListener;

    private Context mContext;
    private List<Video> mVideos;
    private VideosAdapter mAdapter;

    //UI references
    LinearLayout mEmptyLayout;
    RecyclerView mVideosListView;
    TextView mEmptyTextView;

    public interface OnVideosActionListener {
        void onVideoSelected(Video video);
    }

    public VideosFragment() {
        // Required empty public constructor
    }

    public static VideosFragment newInstance() {
        return new VideosFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        mVideos = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setUpListeners();
        setUpVideoList();

        refreshVideos();
    }

    private void initUI(View view){
        mVideosListView = view.findViewById(R.id.videos_list);
        mEmptyLayout = view.findViewById(R.id.videos_empty);
        mEmptyTextView = view.findViewById(R.id.videos_empty_text);
    }

    private void setUpListeners(){

    }

    private void setUpVideoList(){
        mAdapter = new VideosAdapter(mVideos, this);
        mVideosListView.setAdapter(mAdapter);
    }

    private void refreshVideos(){
        checkStoragePermissions();
    }

    private void updateVideosAdapter(List<Video> newList){
        mVideos = newList;
        mAdapter.update(mVideos);

        onVideoListUpdated();
    }

    private void onVideoListUpdated(){
        if(mVideos.size() > 0){
            mEmptyLayout.setVisibility(View.GONE);
            mVideosListView.setVisibility(View.VISIBLE);
        }
        else{
            mEmptyLayout.setVisibility(View.VISIBLE);
            mVideosListView.setVisibility(View.GONE);
        }
    }

    private void checkStoragePermissions(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(getContext(), permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                storagePermissionsGranted();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                storagePermissionsDenied();
            }
        });
    }

    private void storagePermissionsGranted(){
        ModelFilterUtils.getVideos(getActivity(), new FilterResultCallback<Video>() {
            @Override
            public void onResult(List<Video> videos) {
                updateVideosAdapter(videos);
            }
        });
    }

    private void storagePermissionsDenied(){
        Activity activity = getActivity();

        if(activity != null){
            Toast.makeText(getContext(), "Please enable the required permissions to continue", Toast.LENGTH_SHORT).show();
            activity.finish();
        }

    }

    @Override
    public void onVideoClick(View v, int position) {
        if(mListener != null){
            Video video = mVideos.get(position);
            mListener.onVideoSelected(video);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnVideosActionListener) {
            mListener = (OnVideosActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnVideosActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
