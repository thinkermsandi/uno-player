package za.co.rationalthinkers.unoplayer.android.loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.model.Album;
import za.co.rationalthinkers.unoplayer.android.model.Artist;
import za.co.rationalthinkers.unoplayer.android.model.Playlist;
import za.co.rationalthinkers.unoplayer.android.model.Song;

public class PlaylistLoader extends CursorLoader {

    public static final String[] PLAYLIST_PROJECTION = {
            MediaStore.Audio.Playlists._ID,
            MediaStore.Audio.Playlists.NAME,
            MediaStore.Audio.Playlists.DATE_ADDED
    };
    public static final String[] PLAYLIST_SONGS_PROJECTION = {
            MediaStore.Audio.Playlists.Members._ID,
            MediaStore.Audio.Playlists.Members.PLAYLIST_ID,
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
            MediaStore.Audio.Playlists.Members.CONTENT_DIRECTORY,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER,
            MediaStore.Audio.Playlists.Members.TITLE,
            MediaStore.Audio.Playlists.Members.DURATION,
            MediaStore.Audio.Playlists.Members.SIZE,
            MediaStore.Audio.Playlists.Members.TRACK,
            MediaStore.Audio.Playlists.Members.YEAR,
            MediaStore.Audio.Playlists.Members.ARTIST_ID,
            MediaStore.Audio.Playlists.Members.ARTIST,
            MediaStore.Audio.Playlists.Members.ALBUM_ID,
            MediaStore.Audio.Playlists.Members.ALBUM,
            MediaStore.Audio.Playlists.Members.DATA,
            MediaStore.Audio.Playlists.Members.DATE_ADDED
    };
    public static final String PLAYLIST_SORT_ORDER = MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER;
    public static final String PLAYLIST_SONGS_SORT_ORDER = MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER;

    public PlaylistLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    public PlaylistLoader(Context context) {
        super(context);

        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String selection = null;
        String[] selectionArgs = null;

        setProjection(PLAYLIST_PROJECTION);
        setUri(uri);
        setSelection(selection);
        setSelectionArgs(selectionArgs);
        setSortOrder(PLAYLIST_SORT_ORDER);
    }

    public static ArrayList<Playlist> getPlaylists(Context context, String selection, String selectionArgs[], String sortOrder) {
        ArrayList<Playlist> playlists = new ArrayList<>();

        if (context == null) {
            return playlists;
        }

        Cursor cursor = getPlaylistsCursor(context, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {

                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    long timestamp = cursor.getLong(cursor.getColumnIndex("date_added"));

                    Date date = DateTimeUtils.formatDate(timestamp);

                    Playlist playlist = new Playlist();
                    playlist.setName(name);
                    //playlist.setSongs(getSongsFromPlaylistId(context, id));

                    playlists.add(playlist);

                }
            } catch (Exception e) {

            } finally {
                cursor.close();
            }

        }

        return playlists;

    }

    private static Cursor getPlaylistsCursor(Context context, String selection, String selectionArgs[], String sortOrder){
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{"_id", "name", "date_added"};

        return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

    }

    public static int getNumberOfSongsFromPlaylistId(Context context, String id){
        int songs = 0;

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(id));
        String[] projection = new String[]{"_id"};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != ?";
        String[] selectionArgs = new String[]{"0"};

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        if(cursor != null){
            try{
                songs = cursor.getCount();
            }
            catch (Exception e){

            }
            finally {
                cursor.close();
            }

        }

        return songs;
    }

    private static List<Song> getSongsFromPlaylistId(Context context, String id){
        List<Song> songs = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(id));
        String[] projection = null;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != ?";
        String[] selectionArgs = new String[]{"0"};

        Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, null);
        if(cursor != null){
            try{
                if(cursor.moveToFirst()){
                    String songId = cursor.getString(cursor.getColumnIndex("audio_id"));

                    songs.add(getSongFromId(context, songId));
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

    private static Song getSongFromId(Context context, String id){
        Song song = new Song();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Uri.encode(id));
        String[] projection = new String[]{"_id", "title", "duration", "_size", "artist_id", "album_id", "date_added"};
        String selection = "is_music = ? AND title != ?";
        String[] selectionArgs = new String[]{"1", ""};
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

                    song.setId(id);
                    song.setTitle(title);
                    song.setDuration(duration);
                    song.setArtist(artist);
                    song.setAlbum(album);
                }
            }
            catch (Exception e){

            }
            finally {
                cursor.close();
            }

        }

        return song;
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

    public static Uri addPlaylist(Context context, Playlist playlist) {
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, playlist.getName());

        return contentResolver.insert(uri, values);
    }

    public static int removePlaylist(Context context, Playlist playlist) {
        ContentResolver contentResolver = context.getContentResolver();
        int playlistId = Integer.parseInt(playlist.getId());

        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String where = MediaStore.Audio.Playlists._ID + " = " + playlistId;

        return contentResolver.delete(uri, where, null);
    }

    public static Uri addSongToPlaylist(Context context, String playlistId, Song song) {
        ContentResolver contentResolver = context.getContentResolver();
        int audioId = Integer.parseInt(song.getId());
        int base = 0;

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(playlistId));
        String[] projection = new String[] { "count(*)" };

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if(cursor != null){
            try{
                if(cursor.moveToFirst()){
                    base = cursor.getInt(0);
                }
            }
            catch (Exception e){

            }
            finally {
                cursor.close();
            }
        }

        ContentValues values = new ContentValues();
        if(base > 0){
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + audioId));
        }
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);

        return contentResolver.insert(uri, values);
    }

    public static int removeSongFromPlaylist(Context context, String playlistId, Song song) {
        ContentResolver contentResolver = context.getContentResolver();
        int audioId = Integer.parseInt(song.getId());

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(playlistId));
        String where = MediaStore.Audio.Playlists.Members.AUDIO_ID + " = " + audioId;

        return contentResolver.delete(uri, where, null);
    }

}
