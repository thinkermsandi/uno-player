package za.co.rationalthinkers.unoplayer.android.callback;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.loader.VideoLoader;
import za.co.rationalthinkers.unoplayer.android.model.Video;

import static za.co.rationalthinkers.unoplayer.android.loader.VideoLoader.VIDEO_PROJECTION;
import static za.co.rationalthinkers.unoplayer.android.loader.VideoLoader.VIDEO_SORT_ORDER;

public class VideoLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TYPE_VIDEO = 0;
    public static final int TYPE_VIDEOS = 1;

    private WeakReference<Context> context;
    private FilterResultCallback resultCallback;

    private int mType = TYPE_VIDEOS;
    private String arg = null;
    private CursorLoader mLoader;

    public VideoLoaderCallback(Context context, FilterResultCallback resultCallback, int type) {
        this(context, resultCallback, type, null);
    }

    public VideoLoaderCallback(Context context, FilterResultCallback resultCallback, int type, String arg) {
        this.context = new WeakReference<>(context);
        this.resultCallback = resultCallback;
        this.mType = type;
        this.arg = arg;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (mType) {

            case TYPE_VIDEO:
                Uri videoUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Uri.encode(arg));
                String videoSelection = null;
                String[] videoSelectionArgs = null;

                mLoader = new VideoLoader(context.get(),videoUri, VIDEO_PROJECTION, videoSelection, videoSelectionArgs, VIDEO_SORT_ORDER);
                break;

            case TYPE_VIDEOS:
                mLoader = new VideoLoader(context.get());
                break;

        }

        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        switch (mType) {
            case TYPE_VIDEO:
                onVideoResult(data);
                break;

            case TYPE_VIDEOS:
                onVideoResult(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @SuppressWarnings("unchecked")
    private void onVideoResult(final Cursor data) {
        List<Video> videos = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            //Create a File instance
            Video video = new Video();
            video.setId(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
            video.setTitle(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
            video.setDuration(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
            video.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
            video.setResolution(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)));
            video.setWidth(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)));
            video.setHeight(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)));
            video.setArtistName(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)));
            video.setAlbumTitle(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)));
            video.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
            video.setDateAdded(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)));

            videos.add(video);
        }

        if (resultCallback != null) {
            resultCallback.onResult(videos);
        }
    }

}
