package za.co.rationalthinkers.unoplayer.android.async;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import za.co.rationalthinkers.unoplayer.android.model.Video;
import za.co.rationalthinkers.unoplayer.android.viewmodel.VideoViewModel;

public class LoadVideosAsync extends AsyncTask<String, Void, Boolean> {

    private WeakReference<VideoViewModel> _model;
    private Context context;
    private ArrayList<Video> videos = new ArrayList<>();

    public LoadVideosAsync(VideoViewModel model){
        this._model = new WeakReference<>(model);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        VideoViewModel model = _model.get();

        if(model != null){
            context = model.getContext();
            Cursor cursor = getVideosCursor();

            if (cursor != null) {
                try{
                    while(cursor.moveToNext()) {

                        String id = cursor.getString(cursor.getColumnIndex("_id"));
                        String path = cursor.getString(cursor.getColumnIndex("_data"));
                        String displayName = cursor.getString(cursor.getColumnIndex("_display_name"));
                        String title = cursor.getString(cursor.getColumnIndex("title"));
                        String mimeType = cursor.getString(cursor.getColumnIndex("mime_type"));
                        long duration = cursor.getLong(cursor.getColumnIndex("duration"));
                        long size = cursor.getLong(cursor.getColumnIndex("_size"));
                        long timestamp = cursor.getLong(cursor.getColumnIndex("date_added"));

                        Date date = DateTimeUtils.formatDate(timestamp);

                        Video video = new Video();
                        video.setId(id);
                        video.setPath(path);
                        video.setTitle(displayName);
                        video.setSize(size);
                        video.setDuration(duration);

                        videos.add(video);

                    }
                }
                catch (Exception e){

                }
                finally {
                    cursor.close();
                }
            }

            return true;
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        VideoViewModel model = _model.get();

        if(model != null){
            model.updateVideos(videos);
        }
    }

    private Cursor getVideosCursor(){
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{"_id", "_data", "_display_name", "title", "mime_type", "duration", "_size", "date_added"};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Video.Media.DATE_ADDED;

        return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

    }

}
