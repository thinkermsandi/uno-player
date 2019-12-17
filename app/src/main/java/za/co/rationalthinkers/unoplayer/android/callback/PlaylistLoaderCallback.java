package za.co.rationalthinkers.unoplayer.android.callback;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import za.co.rationalthinkers.unoplayer.android.loader.PlaylistLoader;
import za.co.rationalthinkers.unoplayer.android.model.Playlist;
import za.co.rationalthinkers.unoplayer.android.model.Song;

import static za.co.rationalthinkers.unoplayer.android.loader.PlaylistLoader.PLAYLIST_PROJECTION;
import static za.co.rationalthinkers.unoplayer.android.loader.PlaylistLoader.PLAYLIST_SONGS_PROJECTION;
import static za.co.rationalthinkers.unoplayer.android.loader.PlaylistLoader.PLAYLIST_SONGS_SORT_ORDER;
import static za.co.rationalthinkers.unoplayer.android.loader.PlaylistLoader.PLAYLIST_SORT_ORDER;

public class PlaylistLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TYPE_PLAYLIST = 0;
    public static final int TYPE_PLAYLIST_SONGS = 1;
    public static final int TYPE_PLAYLISTS = 2;

    private WeakReference<Context> context;
    private FilterResultCallback resultCallback;

    private int mType = TYPE_PLAYLISTS;
    private String arg = null;
    private CursorLoader mLoader;

    public PlaylistLoaderCallback(Context context, FilterResultCallback resultCallback, int type) {
        this(context, resultCallback, type, null);
    }

    public PlaylistLoaderCallback(Context context, FilterResultCallback resultCallback, int type, String arg) {
        this.context = new WeakReference<>(context);
        this.resultCallback = resultCallback;
        this.mType = type;
        this.arg = arg;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (mType) {

            case TYPE_PLAYLIST:
                Uri playlistUri = Uri.withAppendedPath(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, Uri.encode(arg));
                String playlistSelection = null;
                String[] playlistSelectionArgs = null;

                mLoader = new PlaylistLoader(context.get(), playlistUri, PLAYLIST_PROJECTION, playlistSelection, playlistSelectionArgs, PLAYLIST_SORT_ORDER);
                break;

            case TYPE_PLAYLIST_SONGS:
                Uri playlistSongsUri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(arg));
                String playlistSongsSelection = null;
                String[] playlistSongsSelectionArgs = null;

                mLoader = new PlaylistLoader(context.get(), playlistSongsUri, null, playlistSongsSelection, playlistSongsSelectionArgs, PLAYLIST_SONGS_SORT_ORDER);
                break;

            case TYPE_PLAYLISTS:
                mLoader = new PlaylistLoader(context.get());
                break;

        }

        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        switch (mType) {
            case TYPE_PLAYLIST:
                onPlaylistResult(data);
                break;

            case TYPE_PLAYLIST_SONGS:
                onSongResult(data);
                break;

            case TYPE_PLAYLISTS:
                onPlaylistResult(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @SuppressWarnings("unchecked")
    private void onPlaylistResult(final Cursor data) {
        List<Playlist> playlists = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            String id = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID));

            Playlist playlist = new Playlist();
            playlist.setId(id);
            playlist.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME)));
            playlist.setNumberOfSongs(PlaylistLoader.getNumberOfSongsFromPlaylistId(context.get(), id));
            playlist.setDateAdded(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.DATE_ADDED)));

            playlists.add(playlist);
        }

        if (resultCallback != null) {
            resultCallback.onResult(playlists);
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
            song.setId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
            song.setTitle(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.TITLE)));
            song.setDuration(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DURATION)));
            song.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.SIZE)));
            song.setTrackNumber(data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.TRACK)));
            song.setYear(data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.YEAR)));
            song.setArtistId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ARTIST_ID)));
            song.setArtistName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ARTIST)));
            song.setAlbumId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM_ID)));
            song.setAlbumTitle(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM)));
            song.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DATA)));
            song.setDateAdded(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DATE_ADDED)));

            songs.add(song);
        }

        if (resultCallback != null) {
            resultCallback.onResult(songs);
        }
    }

}
