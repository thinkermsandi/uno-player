package za.co.rationalthinkers.unoplayer.android.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

public class VideoLoader extends CursorLoader {

    public static final String[] VIDEO_PROJECTION = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.RESOLUTION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media.ALBUM,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED
    };
    public static final String VIDEO_SORT_ORDER = MediaStore.Video.Media.DEFAULT_SORT_ORDER;

    public VideoLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    public VideoLoader(Context context) {
        super(context);

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = null;
        String[] selectionArgs = null;

        setProjection(VIDEO_PROJECTION);
        setUri(uri);
        setSelection(selection);
        setSelectionArgs(selectionArgs);
        setSortOrder(VIDEO_SORT_ORDER);
    }

    public static String getVideoThumbnailPath(Context context, String videoFilePath){
        String result = null;

        ContentResolver contentResolver = context.getContentResolver();

        long videoId = getVideoIdFromPath(context, videoFilePath);
        if (videoId > 0) {
            Uri uri = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
            String[] projection = { MediaStore.Video.Thumbnails.DATA };
            String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
            String[] selectionArgs = { String.valueOf(videoId) };

            Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex("_data"));
                    }
                }
                catch (Exception e) {

                }
                finally {
                    cursor.close();
                }

            }
        }

        return result;
    }

    public static String getVideoReadableDuration(Context context, String videoFilePath){
        String result = null;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoFilePath);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );

        retriever.release();

        return result;
    }

    private static long getVideoIdFromPath(Context context, String videoFilePath){
        long videoId = -1;

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { "_id" };
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = { videoFilePath };
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    videoId = cursor.getLong(cursor.getColumnIndex("_id"));
                }
            }
            catch (Exception e) {

            }
            finally {
                cursor.close();
            }

        }

        return videoId;
    }

}
