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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.adapter.SongsAdapter;
import za.co.rationalthinkers.unoplayer.android.callback.FilterResultCallback;
import za.co.rationalthinkers.unoplayer.android.config.Constants;
import za.co.rationalthinkers.unoplayer.android.model.Playlist;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.util.ModelFilterUtils;

public class PlaylistDetailFragment extends Fragment
        implements SongsAdapter.OnItemClickListener {

    private OnPlaylistDetailActionListener mListener;

    private Context mContext;
    private Playlist mPlaylist;
    private List<Song> mSongs;
    private SongsAdapter mAdapter;

    //UI references
    RecyclerView mSongListView;
    private ImageView playlistImageView;
    private TextView playlistNameView;
    private TextView playlistDetailsView;

    public interface OnPlaylistDetailActionListener {
        void onPlaylistSongSelected(ArrayList<Song> songs, Song song);
        void onPlaylistDetailExit();
    }

    public PlaylistDetailFragment() {
        // Required empty public constructor
    }

    public static PlaylistDetailFragment newInstance(Playlist playlist) {
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();

        Bundle data = new Bundle();
        data.putParcelable(Constants.ARG_PLAYLIST, playlist);
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        if (getArguments() != null) {
            mPlaylist = getArguments().getParcelable(Constants.ARG_PLAYLIST);
        }

        mSongs = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setUpListeners();

        setUpSongList();
        setUpPlaylistDetails();

        getData();
    }

    private void initUI(View view){
        mSongListView = view.findViewById(R.id.playlist_list);
        playlistImageView = view.findViewById(R.id.playlist_image);
        playlistNameView = view.findViewById(R.id.playlist_name);
        playlistDetailsView = view.findViewById(R.id.playlist_info);
    }

    private void setUpListeners(){

    }

    private void setUpSongList(){
        mAdapter = new SongsAdapter(mSongs, this);
        mSongListView.setAdapter(mAdapter);
    }

    private void setUpPlaylistDetails(){
        String details = mPlaylist.getNumberOfSongs() + " Songs";

        playlistNameView.setText(mPlaylist.getName());
        playlistDetailsView.setText(details);
    }

    private void getData(){
        checkStoragePermissions();
    }

    private void updateSongsList(List<Song> songs){
        mSongs = songs;
        mAdapter.update(songs);
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
        ModelFilterUtils.getPlaylistSongsFromId(getActivity(), mPlaylist.getId(), new FilterResultCallback<Song>() {
            @Override
            public void onResult(List<Song> songs) {
                updateSongsList(songs);
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
            mListener.onPlaylistSongSelected(new ArrayList<>(mSongs), song);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnPlaylistDetailActionListener) {
            mListener = (OnPlaylistDetailActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnPlaylistDetailActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
