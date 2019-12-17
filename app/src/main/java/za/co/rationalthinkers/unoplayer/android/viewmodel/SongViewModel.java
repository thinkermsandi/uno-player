package za.co.rationalthinkers.unoplayer.android.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import za.co.rationalthinkers.unoplayer.android.async.LoadSongsAsync;
import za.co.rationalthinkers.unoplayer.android.model.Song;

public class SongViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<List<Song>> songs;

    public SongViewModel(@NonNull Application application) {
        super(application);

        this.context = application.getApplicationContext();
    }

    public Context getContext(){
        return context;
    }

    public LiveData<List<Song>> getSongs(){
        if (songs == null) {
            songs = new MutableLiveData<List<Song>>();
            loadSongs();
        }

        return songs;
    }

    private void loadSongs() {
        new LoadSongsAsync(this).execute();
    }

    public void updateSongs(List<Song> newSongs) {
        songs.postValue(newSongs);
    }

}
