package za.co.rationalthinkers.unoplayer.android.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;

import za.co.rationalthinkers.unoplayer.android.model.Album;
import za.co.rationalthinkers.unoplayer.android.model.Artist;
import za.co.rationalthinkers.unoplayer.android.model.Song;

public class SongLoader extends CursorLoader {

    public static final String[] SONG_PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED
    };
    public static final String SONG_SORT_ORDER = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    public SongLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    public SongLoader(Context context) {
        super(context);

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = "is_music = ? AND title != ?";
        String[] selectionArgs = new String[]{"1", ""};

        setProjection(SONG_PROJECTION);
        setUri(uri);
        setSelection(selection);
        setSelectionArgs(selectionArgs);
        setSortOrder(SONG_SORT_ORDER);
    }

    public static ArrayList<Song> getSongs(Context context, String selection, String selectionArgs[], String sortOrder) {
        ArrayList<Song> songs = new ArrayList<>();

        if (context == null) {
            return songs;
        }

        Cursor cursor = getSongsCursor(context, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {

                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    long duration = cursor.getLong(cursor.getColumnIndex("duration"));
                    long size = cursor.getLong(cursor.getColumnIndex("_size"));
                    String artistId = cursor.getString(cursor.getColumnIndex("artist_id"));
                    String albumId = cursor.getString(cursor.getColumnIndex("album_id"));
                    long timestamp = cursor.getLong(cursor.getColumnIndex("date_added"));

                    Date date = DateTimeUtils.formatDate(timestamp);
                    //Artist artist = getArtistFromId(context, artistId);
                    //Album album = getAlbumFromId(context, albumId);

                    Song song = new Song();
                    song.setId(id);
                    song.setTitle(title);
                    song.setDuration(duration);
                    //song.setArtistName(artist);
                    //song.setAlbum(album);

                    songs.add(song);

                }
            } catch (Exception e) {

            } finally {
                cursor.close();
            }

        }

        return songs;

    }


    private static Cursor getSongsCursor(Context context, String selection, String selectionArgs[],String sortOrder){
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{"_id", "title", "duration", "_size", "artist_id", "album_id", "date_added"};

        return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

    }

    private static Artist getArtistFromId (Context context, String id){
        Artist artist = new Artist();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, Uri.encode(id));
        String[] projection = {"_id", "artist", "number_of_albums", "number_of_tracks"};

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    //String id = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("artist"));
                    int noOfAlbums = cursor.getInt(cursor.getColumnIndex("number_of_albums"));
                    int noOfSongs = cursor.getInt(cursor.getColumnIndex("number_of_tracks"));

                    artist.setName(name);
                }
            } catch (Exception e) {

            } finally {
                cursor.close();
            }

        }

        return artist;

    }

    private static Album getAlbumFromId (Context context, String id){
        Album album = new Album();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, Uri.encode(id));
        String[] projection = {"_id", "album", "album_art", "artist_id", "numsongs"};

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    //String id = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("album"));
                    String art = cursor.getString(cursor.getColumnIndex("album_art"));
                    String artistId = cursor.getString(cursor.getColumnIndex("artist_id"));
                    int noOfSongs = cursor.getInt(cursor.getColumnIndex("numsongs"));

                    album.setName(name);
                }
            } catch (Exception e) {

            } finally {
                cursor.close();
            }

        }

        return album;

    }

}
