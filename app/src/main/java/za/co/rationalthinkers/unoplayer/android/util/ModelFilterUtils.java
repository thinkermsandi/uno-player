package za.co.rationalthinkers.unoplayer.android.util;

import android.support.v4.app.FragmentActivity;

import za.co.rationalthinkers.unoplayer.android.callback.FilterResultCallback;
import za.co.rationalthinkers.unoplayer.android.callback.MusicLoaderCallback;
import za.co.rationalthinkers.unoplayer.android.callback.PlaylistLoaderCallback;
import za.co.rationalthinkers.unoplayer.android.callback.VideoLoaderCallback;
import za.co.rationalthinkers.unoplayer.android.model.Album;
import za.co.rationalthinkers.unoplayer.android.model.Artist;
import za.co.rationalthinkers.unoplayer.android.model.Playlist;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.model.Video;

import static za.co.rationalthinkers.unoplayer.android.callback.MusicLoaderCallback.TYPE_ALBUM;
import static za.co.rationalthinkers.unoplayer.android.callback.MusicLoaderCallback.TYPE_ALBUMS;
import static za.co.rationalthinkers.unoplayer.android.callback.MusicLoaderCallback.TYPE_ALBUMS_ARTIST;
import static za.co.rationalthinkers.unoplayer.android.callback.MusicLoaderCallback.TYPE_ARTIST;
import static za.co.rationalthinkers.unoplayer.android.callback.MusicLoaderCallback.TYPE_ARTISTS;
import static za.co.rationalthinkers.unoplayer.android.callback.MusicLoaderCallback.TYPE_SONG;
import static za.co.rationalthinkers.unoplayer.android.callback.MusicLoaderCallback.TYPE_SONGS;
import static za.co.rationalthinkers.unoplayer.android.callback.MusicLoaderCallback.TYPE_SONGS_ALBUM;
import static za.co.rationalthinkers.unoplayer.android.callback.MusicLoaderCallback.TYPE_SONGS_ARTIST;
import static za.co.rationalthinkers.unoplayer.android.callback.PlaylistLoaderCallback.TYPE_PLAYLIST;
import static za.co.rationalthinkers.unoplayer.android.callback.PlaylistLoaderCallback.TYPE_PLAYLISTS;
import static za.co.rationalthinkers.unoplayer.android.callback.PlaylistLoaderCallback.TYPE_PLAYLIST_SONGS;
import static za.co.rationalthinkers.unoplayer.android.callback.VideoLoaderCallback.TYPE_VIDEO;
import static za.co.rationalthinkers.unoplayer.android.callback.VideoLoaderCallback.TYPE_VIDEOS;

public class ModelFilterUtils {

    public static void getAlbums(FragmentActivity activity, FilterResultCallback<Album> callback){
        activity.getSupportLoaderManager().initLoader(0, null, new MusicLoaderCallback(activity, callback, TYPE_ALBUMS));
    }

    public static void getAlbumsFromArtistName(FragmentActivity activity, String name, FilterResultCallback<Album> callback){
        activity.getSupportLoaderManager().restartLoader(1, null, new MusicLoaderCallback(activity, callback, TYPE_ALBUMS_ARTIST, name));
    }

    public static void getAlbumFromId(FragmentActivity activity, String id, FilterResultCallback<Album> callback){
        activity.getSupportLoaderManager().restartLoader(2, null, new MusicLoaderCallback(activity, callback, TYPE_ALBUM, id));
    }

    public static void getArtists(FragmentActivity activity, FilterResultCallback<Artist> callback){
        activity.getSupportLoaderManager().initLoader(3, null, new MusicLoaderCallback(activity, callback, TYPE_ARTISTS));
    }

    public static void getArtistFromId(FragmentActivity activity, String id, FilterResultCallback<Artist> callback){
        activity.getSupportLoaderManager().restartLoader(4, null, new MusicLoaderCallback(activity, callback, TYPE_ARTIST, id));
    }

    public static void getSongs(FragmentActivity activity, FilterResultCallback<Song> callback){
        activity.getSupportLoaderManager().initLoader(5, null, new MusicLoaderCallback(activity, callback, TYPE_SONGS));
    }

    public static void getSongsFromArtistId(FragmentActivity activity, String id, FilterResultCallback<Song> callback){
        activity.getSupportLoaderManager().restartLoader(6, null, new MusicLoaderCallback(activity, callback, TYPE_SONGS_ARTIST, id));
    }

    public static void getSongsFromAlbumId(FragmentActivity activity, String id, FilterResultCallback<Song> callback){
        activity.getSupportLoaderManager().restartLoader(7, null, new MusicLoaderCallback(activity, callback, TYPE_SONGS_ALBUM, id));
    }

    public static void getSongFromId(FragmentActivity activity, String id, FilterResultCallback<Song> callback){
        activity.getSupportLoaderManager().restartLoader(8, null, new MusicLoaderCallback(activity, callback, TYPE_SONG, id));
    }

    public static void getPlaylists(FragmentActivity activity, FilterResultCallback<Playlist> callback){
        activity.getSupportLoaderManager().restartLoader(9, null, new PlaylistLoaderCallback(activity, callback, TYPE_PLAYLISTS));
    }

    public static void getPlaylistFromId(FragmentActivity activity, String id, FilterResultCallback<Playlist> callback){
        activity.getSupportLoaderManager().restartLoader(10, null, new PlaylistLoaderCallback(activity, callback, TYPE_PLAYLIST, id));
    }

    public static void getPlaylistSongsFromId(FragmentActivity activity, String id, FilterResultCallback<Song> callback){
        activity.getSupportLoaderManager().restartLoader(11, null, new PlaylistLoaderCallback(activity, callback, TYPE_PLAYLIST_SONGS, id));
    }

    public static void getVideos(FragmentActivity activity, FilterResultCallback<Video> callback){
        activity.getSupportLoaderManager().initLoader(12, null, new VideoLoaderCallback(activity, callback, TYPE_VIDEOS));
    }

    public static void getVideoFromId(FragmentActivity activity, String id, FilterResultCallback<Video> callback){
        activity.getSupportLoaderManager().restartLoader(13, null, new VideoLoaderCallback(activity, callback, TYPE_VIDEO, id));
    }

}
