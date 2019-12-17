package za.co.rationalthinkers.unoplayer.android.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import za.co.rationalthinkers.unoplayer.android.async.LoadVideosAsync;
import za.co.rationalthinkers.unoplayer.android.model.Video;

public class VideoViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<List<Video>> video;

    public VideoViewModel(@NonNull Application application) {
        super(application);

        this.context = application.getApplicationContext();
    }

    public Context getContext(){
        return context;
    }

    public LiveData<List<Video>> getVideos(){
        if (video == null) {
            video = new MutableLiveData<List<Video>>();
            loadVideos();
        }

        return video;
    }

    private void loadVideos() {
        new LoadVideosAsync(this).execute();
    }

    public void updateVideos(List<Video> newVideos) {
        video.postValue(newVideos);
    }

}
