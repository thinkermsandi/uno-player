package za.co.rationalthinkers.unoplayer.android.async;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Log;

import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.model.Album;
import za.co.rationalthinkers.unoplayer.android.model.Artist;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.viewmodel.AlbumViewModel;

public class LoadAlbumsAsync extends AsyncTask<String, Void, Boolean> {

    private WeakReference<AlbumViewModel> _model;
    private Context context;
    private ArrayList<Album> albums = new ArrayList<>();

    public LoadAlbumsAsync(AlbumViewModel model){
        this._model = new WeakReference<>(model);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        AlbumViewModel model = _model.get();

        if(model != null){
            context = model.getContext();
            Cursor cursor = getAlbumsCursor();

            if (cursor != null) {
                try{
                    while(cursor.moveToNext()) {

                        String id = cursor.getString(cursor.getColumnIndex("_id"));
                        String name = cursor.getString(cursor.getColumnIndex("album"));
                        String art = cursor.getString(cursor.getColumnIndex("album_art"));
                        String artistId = cursor.getString(cursor.getColumnIndex("artist_id"));
                        int noOfSongs = cursor.getInt(cursor.getColumnIndex("numsongs"));

                        Album album = new Album();
                        album.setName(name);
                        album.setSongs(getSongsFromAlbumId(id));

                        albums.add(album);

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
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
        AlbumViewModel model = _model.get();

        if(model != null){
            model.updateAlbums(albums);
        }
    }

    private Cursor getAlbumsCursor(){
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{"_id", "album", "album_art", "artist_id", "numsongs"};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

    }

    private List<Song> getSongsFromAlbumId(String id){
        List<Song> songs = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{"_id", "title", "duration", "_size", "artist_id", "album_id", "date_added"};
        String selection = "album_id = ? AND is_music = ? AND title != ?";
        String[] selectionArgs = new String[]{id, "1", ""};
        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
        if(cursor != null){
            try{
                if(cursor.moveToFirst()){
                    //String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    long duration = cursor.getLong(cursor.getColumnIndex("duration"));
                    long size = cursor.getLong(cursor.getColumnIndex("_size"));
                    String artistId = cursor.getString(cursor.getColumnIndex("artist_id"));
                    String albumId = cursor.getString(cursor.getColumnIndex("album_id"));
                    long timestamp = cursor.getLong(cursor.getColumnIndex("date_added"));

                    Date date = DateTimeUtils.formatDate(timestamp);
                    Artist artist = getArtistFromId(artistId);
                    Album album = getAlbumFromId(albumId);

                    Song song = new Song();
                    song.setId(id);
                    song.setTitle(title);
                    song.setDuration(duration);
                    song.setArtist(artist);
                    song.setAlbum(album);

                    songs.add(song);
                }
            }
            catch (Exception e){

            }
            finally {
                cursor.close();
            }

        }

        return songs;
    }

    private Artist getArtistFromId(String id){
        Artist artist = new Artist();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, Uri.encode(id));
        String[] projection = {"_id", "artist", "number_of_albums", "number_of_tracks"};

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if(cursor != null){
            try{
                if(cursor.moveToFirst()){
                    //String id = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("artist"));
                    int noOfAlbums = cursor.getInt(cursor.getColumnIndex("number_of_albums"));
                    int noOfSongs = cursor.getInt(cursor.getColumnIndex("number_of_tracks"));

                    artist.setName(name);
                }
            }
            catch (Exception e){

            }
            finally {
                cursor.close();
            }

        }

        return artist;

    }

    private Album getAlbumFromId(String id){
        Album album = new Album();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, Uri.encode(id));
        String[] projection = {"_id", "album", "album_art", "artist_id", "numsongs"};

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if(cursor != null){
            try{
                if(cursor.moveToFirst()){
                    //String id = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("album"));
                    String art = cursor.getString(cursor.getColumnIndex("album_art"));
                    String artistId = cursor.getString(cursor.getColumnIndex("artist_id"));
                    int noOfSongs = cursor.getInt(cursor.getColumnIndex("numsongs"));

                    album.setName(name);
                }
            }
            catch (Exception e){

            }
            finally {
                cursor.close();
            }

        }

        return album;

    }

}
