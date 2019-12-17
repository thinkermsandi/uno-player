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
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.model.Album;
import za.co.rationalthinkers.unoplayer.android.model.Artist;
import za.co.rationalthinkers.unoplayer.android.model.Song;

public class ArtistLoader extends CursorLoader {

    public static final String[] ARTIST_PROJECTION = {
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
    };
    public static final String ARTIST_SORT_ORDER = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;

    public ArtistLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    public ArtistLoader(Context context) {
        super(context);

        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String selection = null;
        String[] selectionArgs = null;

        setProjection(ARTIST_PROJECTION);
        setUri(uri);
        setSelection(selection);
        setSelectionArgs(selectionArgs);
        setSortOrder(ARTIST_SORT_ORDER);
    }

    public static ArrayList<Artist> getArtists(Context context, String selection, String selectionArgs[], String sortOrder) {
        ArrayList<Artist> artists = new ArrayList<>();

        if (context == null) {
            return artists;
        }

        Cursor cursor = getArtistsCursor(context, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {

                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String name = cursor.getString(cursor.getColumnIndex("artist"));
                    String noOfAlbums = cursor.getString(cursor.getColumnIndex("number_of_albums"));
                    String noOfSongs = cursor.getString(cursor.getColumnIndex("number_of_tracks"));

                    Artist artist = new Artist();
                    artist.setName(name);
                    //artist.setSongs(getSongsFromArtistId(context, id));
                    //artist.setAlbums(getAlbumsFromArtistId(context, id));

                    artists.add(artist);

                }
            } catch (Exception e) {

            } finally {
                cursor.close();
            }

        }

        return artists;

    }

    private static Cursor getArtistsCursor(Context context, String selection, String selectionArgs[], String sortOrder){
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{"_id", "artist", "number_of_albums", "number_of_tracks"};

        return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

    }

    private static List<Song> getSongsFromArtistId(Context context, String id){
        List<Song> songs = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{"_id", "title", "duration", "_size", "artist_id", "album_id", "date_added"};
        String selection = "artist_id = ? AND is_music = ? AND title != ?";
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
                    Artist artist = getArtistFromId(context, artistId);
                    Album album = getAlbumFromId(context, albumId);

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

    private static List<Album> getAlbumsFromArtistId(Context context, String id){
        List<Album> albums = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{"_id", "album", "album_art", "artist_id", "numsongs"};
        String selection = "artist_id = ?";
        String[] selectionArgs = new String[]{id};
        String sortOrder = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
        if(cursor != null){
            try{
                if(cursor.moveToFirst()){
                    //String id = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("album"));
                    String art = cursor.getString(cursor.getColumnIndex("album_art"));
                    String artistId = cursor.getString(cursor.getColumnIndex("artist_id"));
                    int noOfSongs = cursor.getInt(cursor.getColumnIndex("numsongs"));

                    Album album = new Album();
                    album.setName(name);

                    albums.add(album);
                }
            }
            catch (Exception e){

            }
            finally {
                cursor.close();
            }

        }

        return albums;
    }

    private static Artist getArtistFromId(Context context, String id){
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

    private static Album getAlbumFromId(Context context, String id){
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
