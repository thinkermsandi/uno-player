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
import za.co.rationalthinkers.unoplayer.android.adapter.AlbumsAdapter;
import za.co.rationalthinkers.unoplayer.android.adapter.SongsAdapter;
import za.co.rationalthinkers.unoplayer.android.callback.FilterResultCallback;
import za.co.rationalthinkers.unoplayer.android.config.Constants;
import za.co.rationalthinkers.unoplayer.android.model.Album;
import za.co.rationalthinkers.unoplayer.android.model.Artist;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.util.ModelFilterUtils;

public class ArtistDetailFragment extends Fragment
        implements SongsAdapter.OnItemClickListener,
        AlbumsAdapter.OnItemClickListener {

    private OnArtistDetailActionListener mListener;

    private Context mContext;
    private Artist mArtist;
    private List<Album> mAlbums;
    private List<Song> mSongs;
    private AlbumsAdapter mAlbumsAdapter;
    private SongsAdapter mSongsAdapter;

    //UI references
    RecyclerView mSongListView;
    RecyclerView mAlbumListView;
    private ImageView artistImageView;
    private TextView artistNameView;
    private TextView artistDetailsView;

    public interface OnArtistDetailActionListener {
        void onArtistAlbumSelected(Album album);
        void onArtistSongSelected(ArrayList<Song> songs, Song song);
        void onArtistDetailExit();
    }

    public ArtistDetailFragment() {
        // Required empty public constructor
    }

    public static ArtistDetailFragment newInstance(Artist artist) {
        ArtistDetailFragment fragment = new ArtistDetailFragment();

        Bundle data = new Bundle();
        data.putParcelable(Constants.ARG_ARTIST, artist);
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        if (getArguments() != null) {
            mArtist = getArguments().getParcelable(Constants.ARG_ARTIST);
        }

        mSongs = new ArrayList<>();
        mAlbums = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artist_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setUpListeners();

        setUpAlbumList();
        setUpSongList();
        setUpArtistDetails();

        getData();
    }

    private void initUI(View view){
        mAlbumListView = view.findViewById(R.id.albums_list);
        mSongListView = view.findViewById(R.id.songs_list);
        artistImageView = view.findViewById(R.id.artist_image);
        artistNameView = view.findViewById(R.id.artist_name);
        artistDetailsView = view.findViewById(R.id.artist_info);
    }

    private void setUpListeners(){

    }

    private void setUpAlbumList(){
        mAlbumsAdapter = new AlbumsAdapter(mAlbums, this);
        mAlbumListView.setAdapter(mAlbumsAdapter);
    }

    private void setUpSongList(){
        mSongsAdapter = new SongsAdapter(mSongs, this);
        mSongListView.setAdapter(mSongsAdapter);
    }

    private void setUpArtistDetails(){
        String details = mArtist.getNumberOfAlbums() + " Albums - " + mArtist.getNumberOfSongs() + " Songs";

        artistNameView.setText(mArtist.getName());
        artistDetailsView.setText(details);
    }

    private void getData(){
        checkStoragePermissions();
    }

    private void updateAlbumsList(List<Album> albums){
        mAlbums = albums;
        mAlbumsAdapter.update(albums);
    }

    private void updateSongsList(List<Song> songs){
        mSongs = songs;
        mSongsAdapter.update(songs);
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
        ModelFilterUtils.getAlbumsFromArtistName(getActivity(), mArtist.getName(), new FilterResultCallback<Album>() {
            @Override
            public void onResult(List<Album> albums) {
                updateAlbumsList(albums);
            }
        });

        ModelFilterUtils.getSongsFromArtistId(getActivity(), mArtist.getId(), new FilterResultCallback<Song>() {
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
    public void onAlbumClick(View v, int position) {
        if(mListener != null){
            Album album = mAlbums.get(position);
            mListener.onArtistAlbumSelected(album);
        }
    }

    @Override
    public void onSongClick(View v, int position) {
        if(mListener != null){
            Song song = mSongs.get(position);
            mListener.onArtistSongSelected(new ArrayList<>(mSongs), song);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnArtistDetailActionListener) {
            mListener = (OnArtistDetailActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnArtistDetailActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
