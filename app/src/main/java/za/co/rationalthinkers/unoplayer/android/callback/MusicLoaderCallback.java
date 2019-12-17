package za.co.rationalthinkers.unoplayer.android.callback;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.loader.AlbumLoader;
import za.co.rationalthinkers.unoplayer.android.loader.ArtistLoader;
import za.co.rationalthinkers.unoplayer.android.loader.SongLoader;
import za.co.rationalthinkers.unoplayer.android.model.Album;
import za.co.rationalthinkers.unoplayer.android.model.Artist;
import za.co.rationalthinkers.unoplayer.android.model.Song;

import static za.co.rationalthinkers.unoplayer.android.loader.AlbumLoader.ALBUM_PROJECTION;
import static za.co.rationalthinkers.unoplayer.android.loader.AlbumLoader.ALBUM_SORT_ORDER;
import static za.co.rationalthinkers.unoplayer.android.loader.ArtistLoader.ARTIST_PROJECTION;
import static za.co.rationalthinkers.unoplayer.android.loader.ArtistLoader.ARTIST_SORT_ORDER;
import static za.co.rationalthinkers.unoplayer.android.loader.SongLoader.SONG_PROJECTION;
import static za.co.rationalthinkers.unoplayer.android.loader.SongLoader.SONG_SORT_ORDER;

public class MusicLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TYPE_ALBUM = 0;
    public static final int TYPE_ALBUMS = 1;
    public static final int TYPE_ALBUMS_ARTIST = 2;
    public static final int TYPE_ARTIST = 3;
    public static final int TYPE_ARTISTS = 4;
    public static final int TYPE_SONG = 5;
    public static final int TYPE_SONGS = 6;
    public static final int TYPE_SONGS_ARTIST = 7;
    public static final int TYPE_SONGS_ALBUM = 8;

    private WeakReference<Context> context;
    private FilterResultCallback resultCallback;

    private int mType = TYPE_SONG;
    private String arg = null;
    private CursorLoader mLoader;

    public MusicLoaderCallback(Context context, FilterResultCallback resultCallback, int type) {
        this(context, resultCallback, type, null);
    }

    public MusicLoaderCallback(Context context, FilterResultCallback resultCallback, int type, String arg) {
        this.context = new WeakReference<>(context);
        this.resultCallback = resultCallback;
        this.mType = type;
        this.arg = arg;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (mType) {

            case TYPE_ALBUM:
                Uri albumUri = Uri.withAppendedPath(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, Uri.encode(arg));
                String albumSelection = null;
                String[] albumSelectionArgs = null;

                mLoader = new AlbumLoader(context.get(), albumUri, ALBUM_PROJECTION, albumSelection, albumSelectionArgs, ALBUM_SORT_ORDER);
                break;

            case TYPE_ALBUMS:
                mLoader = new AlbumLoader(context.get());
                break;

            case TYPE_ALBUMS_ARTIST:
                Uri albumArtistUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                String albumArtistSelection = "artist = ? ";
                String[] albumArtistSelectionArgs = new String[] { arg };

                mLoader = new AlbumLoader(context.get(), albumArtistUri, ALBUM_PROJECTION, albumArtistSelection, albumArtistSelectionArgs, ALBUM_SORT_ORDER);
                break;

            case TYPE_ARTIST:
                Uri artistUri = Uri.withAppendedPath(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, Uri.encode(arg));
                String artistSelection = null;
                String[] artistSelectionArgs = null;

                mLoader = new ArtistLoader(context.get(), artistUri, ARTIST_PROJECTION, artistSelection, artistSelectionArgs, ARTIST_SORT_ORDER);
                break;

            case TYPE_ARTISTS:
                mLoader = new ArtistLoader(context.get());
                break;

            case TYPE_SONG:
                Uri songUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Uri.encode(arg));
                String songSelection = "is_music = ? AND title != ? ";
                String[] songSelectionArgs = new String[] { "1", ""};

                mLoader = new SongLoader(context.get(), songUri, SONG_PROJECTION, songSelection, songSelectionArgs, SONG_SORT_ORDER);
                break;

            case TYPE_SONGS:
                mLoader = new SongLoader(context.get());
                break;

            case TYPE_SONGS_ARTIST:
                Uri songArtistUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String songArtistSelection = "is_music = ? AND title != ? AND artist_id = ? ";
                String[] songArtistSelectionArgs = new String[] { "1", "", arg };

                mLoader = new SongLoader(context.get(), songArtistUri, SONG_PROJECTION, songArtistSelection, songArtistSelectionArgs, SONG_SORT_ORDER);
                break;

            case TYPE_SONGS_ALBUM:
                Uri songAlbumUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String songAlbumSelection = "is_music = ? AND album_id = ? ";
                String[] songAlbumSelectionArgs = new String[] { "1", arg };

                mLoader = new SongLoader(context.get(), songAlbumUri, SONG_PROJECTION, songAlbumSelection, songAlbumSelectionArgs, SONG_SORT_ORDER);
                break;

        }

        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        switch (mType) {
            case TYPE_ALBUM:
                onAlbumResult(data);
                break;

            case TYPE_ALBUMS:
                onAlbumResult(data);
                break;

            case TYPE_ALBUMS_ARTIST:
                onAlbumResult(data);
                break;

            case TYPE_ARTIST:
                onArtistResult(data);
                break;

            case TYPE_ARTISTS:
                onArtistResult(data);
                break;

            case TYPE_SONG:
                onSongResult(data);
                break;

            case TYPE_SONGS:
                onSongResult(data);
                break;

            case TYPE_SONGS_ARTIST:
                onSongResult(data);
                break;

            case TYPE_SONGS_ALBUM:
                onSongResult(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @SuppressWarnings("unchecked")
    private void onAlbumResult(final Cursor data) {
        List<Album> albums = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            //Create a File instance
            Album album = new Album();
            album.setId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)));
            album.setAlbumId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)));
            album.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)));
            album.setArtistName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)));
            album.setNumberOfSongs(data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));

            albums.add(album);
        }

        if (resultCallback != null) {
            resultCallback.onResult(albums);
        }
    }

    @SuppressWarnings("unchecked")
    private void onArtistResult(final Cursor data) {
        List<Artist> artists = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            //Create a File instance
            Artist artist = new Artist();
            artist.setId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)));
            artist.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)));
            artist.setNumberOfSongs(data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));
            artist.setNumberOfAlbums(data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));

            artists.add(artist);
        }

        if (resultCallback != null) {
            resultCallback.onResult(artists);
        }
    }

    @SuppressWarnings("unchecked")
    private void onSongResult(Cursor data) {
        List<Song> songs = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            //Create a File instance
            Song song = new Song();
            song.setId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
            song.setTitle(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
            song.setDuration(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
            song.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
            song.setTrackNumber(data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)));
            song.setYear(data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)));
            song.setArtistId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));
            song.setArtistName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
            song.setAlbumId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
            song.setAlbumTitle(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
            song.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
            song.setDateAdded(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)));

            songs.add(song);
        }

        if (resultCallback != null) {
            resultCallback.onResult(songs);
        }
    }

}
