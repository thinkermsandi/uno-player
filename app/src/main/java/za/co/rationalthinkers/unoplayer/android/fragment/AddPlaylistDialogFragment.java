package za.co.rationalthinkers.unoplayer.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.model.Playlist;

public class AddPlaylistDialogFragment extends DialogFragment {

    private OnAddPlaylistActionListener mListener;

    //UI references
    EditText playlistNameText;

    public interface OnAddPlaylistActionListener {
        void onAddPlaylistSelected(Playlist playlist);
        void onAddPlaylistCancelled();
    }

    public AddPlaylistDialogFragment() {
        // Required empty public constructor
    }

    public static AddPlaylistDialogFragment newInstance() {
        return new AddPlaylistDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_dialog_add_playlist, null))
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addPlaylist();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addPlaylistCancel();
                    }
                });

        return builder.create();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_add_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
    }

    private void initUI(View view){
        playlistNameText = view.findViewById(R.id.add_playlist_name);
    }

    private void addPlaylist(){
        String name = playlistNameText.getText().toString();

        if(name.length() > 0){
            Playlist playlist = new Playlist();
            playlist.setName(name);

            if(mListener != null){
                mListener.onAddPlaylistSelected(playlist);
            }
        }

    }

    private void addPlaylistCancel(){
        if(mListener != null){
            mListener.onAddPlaylistCancelled();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnAddPlaylistActionListener) {
            mListener = (OnAddPlaylistActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnAddPlaylistActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
