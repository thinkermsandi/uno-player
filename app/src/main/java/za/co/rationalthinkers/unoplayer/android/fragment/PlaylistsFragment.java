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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.adapter.PlaylistsAdapter;
import za.co.rationalthinkers.unoplayer.android.callback.FilterResultCallback;
import za.co.rationalthinkers.unoplayer.android.loader.PlaylistLoader;
import za.co.rationalthinkers.unoplayer.android.model.Playlist;
import za.co.rationalthinkers.unoplayer.android.util.ModelFilterUtils;

public class PlaylistsFragment extends Fragment
        implements PlaylistsAdapter.OnItemClickListener {

    private OnPlaylistsActionListener mListener;

    private Context mContext;
    private List<Playlist> mPlaylists;
    private PlaylistsAdapter mAdapter;
    //private PlaylistViewModel mPlaylistsViewModel;

    //UI references
    LinearLayout mEmptyLayout;
    RecyclerView mPlaylistsListView;
    TextView mEmptyTextView;

    public interface OnPlaylistsActionListener {
        void onPlaylistSelected(Playlist playlist);
    }

    public PlaylistsFragment() {
        // Required empty public constructor
    }

    public static PlaylistsFragment newInstance() {
        return new PlaylistsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        mPlaylists = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setUpListeners();
        setUpPlaylistList();

        refreshPlaylists();
    }

    private void initUI(View view){
        mPlaylistsListView = view.findViewById(R.id.playlists_list);
        mEmptyLayout = view.findViewById(R.id.playlists_empty);
        mEmptyTextView = view.findViewById(R.id.playlists_empty_text);
    }

    private void setUpListeners(){

    }

    private void setUpPlaylistList(){
        mAdapter = new PlaylistsAdapter(mPlaylists, this);
        mPlaylistsListView.setAdapter(mAdapter);
    }

    private void refreshPlaylists(){
        checkStoragePermissions();
    }

    private void updatePlaylistsAdapter(List<Playlist> newList){
        mPlaylists = newList;

        onPlaylistListUpdated();
        mAdapter.update(mPlaylists);
    }

    private void onPlaylistListUpdated(){
        if(mPlaylists.size() > 0){
            mEmptyLayout.setVisibility(View.GONE);
            mPlaylistsListView.setVisibility(View.VISIBLE);
        }
        else{
            mEmptyLayout.setVisibility(View.VISIBLE);
            mPlaylistsListView.setVisibility(View.GONE);
        }
    }

    public void onPlaylistAdded(Playlist playlist){
        refreshPlaylists();
    }

    private void removePlaylist(Playlist playlist, int positionInList){
        if(PlaylistLoader.removePlaylist(mContext, playlist) > 0){
            mPlaylists.remove(positionInList);
            mAdapter.remove(positionInList);

            Toast.makeText(mContext, "Playlist has been removed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(mContext, "Error removing playlist", Toast.LENGTH_SHORT).show();
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
        ModelFilterUtils.getPlaylists(getActivity(), new FilterResultCallback<Playlist>() {
            @Override
            public void onResult(List<Playlist> playlists) {
                updatePlaylistsAdapter(playlists);
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
    public void onPlaylistClick(View v, int position) {
        if(mListener != null){
            Playlist playlist = mPlaylists.get(position);
            mListener.onPlaylistSelected(playlist);
        }
    }

    @Override
    public void onPlaylistOptionsClick(View v, int position) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.item_playlist_options, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.item_playlist_options_delete:
                        Playlist playlist = mPlaylists.get(position);
                        removePlaylist(playlist, position);
                        break;

                }

                return true;
            }
        });

        popup.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnPlaylistsActionListener) {
            mListener = (OnPlaylistsActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnPlaylistsActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
