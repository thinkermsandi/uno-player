package za.co.rationalthinkers.unoplayer.android.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import za.co.rationalthinkers.unoplayer.android.adapter.SongsAdapter;
import za.co.rationalthinkers.unoplayer.android.callback.FilterResultCallback;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.util.ModelFilterUtils;

public class SongsFragment extends Fragment
        implements SongsAdapter.OnItemClickListener {

    private OnSongsActionListener mListener;

    private Context mContext;
    private List<Song> mSongs;
    private SongsAdapter mAdapter;

    //UI references
    LinearLayout mEmptyLayout;
    RecyclerView mSongsListView;
    TextView mEmptyTextView;

    public interface OnSongsActionListener {
        void onSongSelected(ArrayList<Song>songs, Song song);
    }

    public SongsFragment() {
        // Required empty public constructor
    }

    public static SongsFragment newInstance() {
        return new SongsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        mSongs = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setUpListeners();
        setUpSongList();

        refreshSongs();
    }

    private void initUI(View view){
        mSongsListView = view.findViewById(R.id.songs_list);
        mEmptyLayout = view.findViewById(R.id.songs_empty);
        mEmptyTextView = view.findViewById(R.id.songs_emptyy_text);
    }

    private void setUpListeners(){

    }

    private void setUpSongList(){
        mAdapter = new SongsAdapter(mSongs, this);
        mSongsListView.setAdapter(mAdapter);
    }

    private void refreshSongs(){
        checkStoragePermissions();
    }

    private void updateSongsAdapter(List<Song> newList){
        mSongs = newList;

        onSongListUpdated();
        mAdapter.update(mSongs);
    }

    private void onSongListUpdated(){
        //Log.e("SONGS", "Updating");

        if(mSongs.size() > 0){
            mEmptyLayout.setVisibility(View.GONE);
            mSongsListView.setVisibility(View.VISIBLE);
        }
        else{
            mEmptyLayout.setVisibility(View.VISIBLE);
            mSongsListView.setVisibility(View.GONE);
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
        ModelFilterUtils.getSongs(getActivity(), new FilterResultCallback<Song>() {
            @Override
            public void onResult(List<Song> songs) {
                updateSongsAdapter(songs);
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
    public void onSongClick(View v, int position) {
        if(mListener != null){
            Song song = mSongs.get(position);
            mListener.onSongSelected(new ArrayList<>(mSongs), song);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnSongsActionListener) {
            mListener = (OnSongsActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnSongsActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
