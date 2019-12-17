package za.co.rationalthinkers.unoplayer.android.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import za.co.rationalthinkers.unoplayer.android.async.LoadPlaylistsAsync;
import za.co.rationalthinkers.unoplayer.android.model.Playlist;

public class PlaylistViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<List<Playlist>> playlists;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);

        this.context = application.getApplicationContext();
    }

    public Context getContext(){
        return context;
    }

    public LiveData<List<Playlist>> getPlaylists(){
        if (playlists == null) {
            playlists = new MutableLiveData<List<Playlist>>();
            loadPlaylists();
        }

        return playlists;
    }

    private void loadPlaylists() {
        new LoadPlaylistsAsync(this).execute();
    }

    public void updatePlaylists(List<Playlist> newPlaylists) {
        playlists.postValue(newPlaylists);
    }

}
