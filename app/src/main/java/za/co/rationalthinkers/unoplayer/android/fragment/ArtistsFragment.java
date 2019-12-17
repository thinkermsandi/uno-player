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
import za.co.rationalthinkers.unoplayer.android.adapter.ArtistsAdapter;
import za.co.rationalthinkers.unoplayer.android.callback.FilterResultCallback;
import za.co.rationalthinkers.unoplayer.android.model.Artist;
import za.co.rationalthinkers.unoplayer.android.util.ModelFilterUtils;

public class ArtistsFragment extends Fragment
        implements ArtistsAdapter.OnItemClickListener {

    private OnArtistsActionListener mListener;

    private Context mContext;
    private List<Artist> mArtists;
    private ArtistsAdapter mAdapter;

    //UI references
    LinearLayout mEmptyLayout;
    RecyclerView mArtistsListView;
    TextView mEmptyTextView;

    public interface OnArtistsActionListener {
        void onArtistSelected(Artist artist);
    }

    public ArtistsFragment() {
        // Required empty public constructor
    }

    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        mArtists = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setUpListeners();
        setUpArtistList();

        refreshArtists();
    }

    private void initUI(View view){
        mArtistsListView = view.findViewById(R.id.artists_list);
        mEmptyLayout = view.findViewById(R.id.artists_empty);
        mEmptyTextView = view.findViewById(R.id.artists_empty_text);
    }

    private void setUpListeners(){

    }

    private void setUpArtistList(){
        mAdapter = new ArtistsAdapter(mArtists, this);
        mArtistsListView.setAdapter(mAdapter);
    }

    private void refreshArtists(){
        checkStoragePermissions();
    }

    private void updateArtistsAdapter(List<Artist> newList){
        mArtists = newList;

        onArtistListUpdated();
        mAdapter.update(mArtists);
    }

    private void onArtistListUpdated(){
        if(mArtists.size() > 0){
            mEmptyLayout.setVisibility(View.GONE);
            mArtistsListView.setVisibility(View.VISIBLE);
        }
        else{
            mEmptyLayout.setVisibility(View.VISIBLE);
            mArtistsListView.setVisibility(View.GONE);
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
        ModelFilterUtils.getArtists(getActivity(), new FilterResultCallback<Artist>() {
            @Override
            public void onResult(List<Artist> artists) {
                updateArtistsAdapter(artists);
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
    public void onArtistClick(View v, int position) {
        if(mListener != null){
            Artist artist = mArtists.get(position);
            mListener.onArtistSelected(artist);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnArtistsActionListener) {
            mListener = (OnArtistsActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnArtistsActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
