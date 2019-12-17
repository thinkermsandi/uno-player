package za.co.rationalthinkers.unoplayer.android.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import za.co.rationalthinkers.unoplayer.android.async.LoadArtistsAsync;
import za.co.rationalthinkers.unoplayer.android.model.Artist;

public class ArtistViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<List<Artist>> artists;

    public ArtistViewModel(@NonNull Application application) {
        super(application);

        this.context = application.getApplicationContext();
    }

    public Context getContext(){
        return context;
    }

    public LiveData<List<Artist>> getArtists(){
        if (artists == null) {
            artists = new MutableLiveData<List<Artist>>();
            loadArtists();
        }

        return artists;
    }

    private void loadArtists() {
        new LoadArtistsAsync(this).execute();
    }

    public void updateArtists(List<Artist> newArtists) {
        artists.postValue(newArtists);
    }

}
