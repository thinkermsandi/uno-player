package za.co.rationalthinkers.unoplayer.android.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import za.co.rationalthinkers.unoplayer.android.async.LoadAlbumsAsync;
import za.co.rationalthinkers.unoplayer.android.model.Album;

public class AlbumViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<List<Album>> albums;

    public AlbumViewModel(@NonNull Application application) {
        super(application);

        this.context = application.getApplicationContext();
    }

    public Context getContext(){
        return context;
    }

    public LiveData<List<Album>> getAlbums(){
        if (albums == null) {
            albums = new MutableLiveData<List<Album>>();
            loadAlbums();
        }

        return albums;
    }

    private void loadAlbums() {
        new LoadAlbumsAsync(this).execute();
    }

    public void updateAlbums(List<Album> newAlbums) {
        albums.postValue(newAlbums);
    }

}
