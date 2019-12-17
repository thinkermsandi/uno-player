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
import za.co.rationalthinkers.unoplayer.android.adapter.AlbumsAdapter;
import za.co.rationalthinkers.unoplayer.android.callback.FilterResultCallback;
import za.co.rationalthinkers.unoplayer.android.model.Album;
import za.co.rationalthinkers.unoplayer.android.util.ModelFilterUtils;

public class AlbumsFragment extends Fragment
        implements AlbumsAdapter.OnItemClickListener {

    private OnAlbumsActionListener mListener;

    private Context mContext;
    private List<Album> mAlbums;
    private AlbumsAdapter mAdapter;

    //UI references
    LinearLayout mEmptyLayout;
    RecyclerView mAlbumListView;
    TextView mEmptyTextView;

    public interface OnAlbumsActionListener {
        void onAlbumSelected(Album album);
    }

    public AlbumsFragment() {
        // Required empty public constructor
    }

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        mAlbums = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setUpListeners();
        setUpAlbumList();

        refreshAlbums();
    }

    private void initUI(View view){
        mAlbumListView = view.findViewById(R.id.albums_list);
        mEmptyLayout = view.findViewById(R.id.albums_empty);
        mEmptyTextView = view.findViewById(R.id.albums_empty_text);
    }

    private void setUpListeners(){

    }

    private void setUpAlbumList(){
        mAdapter = new AlbumsAdapter(mAlbums, this);
        mAlbumListView.setAdapter(mAdapter);
    }

    private void refreshAlbums(){
        checkStoragePermissions();
    }

    private void updateAlbumsAdapter(List<Album> newList){
        mAlbums = newList;

        onAlbumListUpdated();
        mAdapter.update(mAlbums);
    }

    private void onAlbumListUpdated(){
        if(mAlbums.size() > 0){
            mEmptyLayout.setVisibility(View.GONE);
            mAlbumListView.setVisibility(View.VISIBLE);
        }
        else{
            mEmptyLayout.setVisibility(View.VISIBLE);
            mAlbumListView.setVisibility(View.GONE);
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
        ModelFilterUtils.getAlbums(getActivity(), new FilterResultCallback<Album>() {
            @Override
            public void onResult(List<Album> albums) {
                updateAlbumsAdapter(albums);
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
            mListener.onAlbumSelected(album);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnAlbumsActionListener) {
            mListener = (OnAlbumsActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnAlbumsActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
