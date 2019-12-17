package za.co.rationalthinkers.unoplayer.android.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.WeakHashMap;

import za.co.rationalthinkers.unoplayer.android.loader.SongLoader;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService;

import static za.co.rationalthinkers.unoplayer.android.config.RepeatMode.REPEAT_ALL;
import static za.co.rationalthinkers.unoplayer.android.config.RepeatMode.REPEAT_CURRENT;
import static za.co.rationalthinkers.unoplayer.android.config.RepeatMode.REPEAT_OFF;
import static za.co.rationalthinkers.unoplayer.android.config.ShuffleMode.SHUFFLE_OFF;
import static za.co.rationalthinkers.unoplayer.android.config.ShuffleMode.SHUFFLE_ON;

public class MusicPlayerRemote {

    @Nullable
    public static MusicPlayerService mService;
    private static final WeakHashMap<Context, ServiceBinder> mConnectionMap = new WeakHashMap<>();

    public static ServiceToken bindToService(Context context, ServiceConnection callback) {

        Activity realActivity = ((Activity) context).getParent();
        if (realActivity == null) {
            realActivity = (Activity) context;
        }

        ContextWrapper contextWrapper = new ContextWrapper(realActivity);
        contextWrapper.startService(new Intent(contextWrapper, MusicPlayerService.class));
        ServiceBinder binder = new ServiceBinder(callback);

        if (contextWrapper.bindService(new Intent().setClass(contextWrapper, MusicPlayerService.class), binder, Context.BIND_AUTO_CREATE)) {
            mConnectionMap.put(contextWrapper, binder);
            return new ServiceToken(contextWrapper);
        }

        return null;
    }

    public static void unbindFromService(ServiceToken token) {
        if (token == null) {
            return;
        }

        ContextWrapper mContextWrapper = token.mWrappedContext;
        ServiceBinder mBinder = mConnectionMap.remove(mContextWrapper);

        if (mBinder == null) {
            return;
        }

        mContextWrapper.unbindService(mBinder);

        if (mConnectionMap.isEmpty()) {
            mService = null;
        }
    }

    public static void playOrPause() {
        try {
            if (mService != null) {
                if (mService.isPlaying()) {
                    mService.pause();
                }
                else {
                    mService.play();
                }
            }
        }
        catch (final Exception ignored) {

        }
    }

    public static void playNextSong() {
        if (mService != null) {
            mService.playNextSong(true);
        }
    }

    public static void playPreviousSong() {
        if (mService != null) {
            mService.back(true);
        }
    }

    public static void playSongAt(int position) {
        if (mService != null) {
            mService.playSongAt(position);
        }
    }

    public static void cycleRepeat() {
        if (mService != null) {
            switch (mService.getRepeatMode()) {
                case REPEAT_OFF:
                    mService.setRepeatMode(REPEAT_ALL);
                    break;

                case REPEAT_ALL:
                    mService.setRepeatMode(REPEAT_CURRENT);

                    if (mService.getShuffleMode() != SHUFFLE_OFF) {
                        mService.setShuffleMode(SHUFFLE_OFF);
                    }

                    break;

                default:
                    mService.setRepeatMode(REPEAT_OFF);
                    break;
            }
        }
    }

    public static void cycleShuffle() {
        if (mService != null) {
            switch (mService.getShuffleMode()) {
                case SHUFFLE_OFF:
                    mService.setShuffleMode(SHUFFLE_ON);

                    if (mService.getRepeatMode() == REPEAT_CURRENT) {
                        mService.setRepeatMode(REPEAT_ALL);
                    }
                    break;

                case SHUFFLE_ON:
                    mService.setShuffleMode(SHUFFLE_OFF);
                    break;

                default:
                    break;
            }
        }
    }

    public static boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }

        return false;
    }

    public static Song getCurrentSong(){
        if (mService != null) {
            return mService.getCurrentSong();
        }

        //return new Song();
        return null;
    }

    public static int getShuffleMode() {
        if (mService != null) {
            return mService.getShuffleMode();
        }

        return 0;
    }

    public static void setShuffleMode(int mode) {
        if (mService != null) {
            mService.setShuffleMode(mode);
        }
    }

    public static int getRepeatMode() {
        if (mService != null) {
            return mService.getRepeatMode();
        }

        return 0;
    }

    public static ArrayList<Song> getPlayingQueue() {
        if (mService != null) {
            return mService.getPlayingQueue();
        }
        else {

        }

        return new ArrayList<>();
    }

    public static void openQueue(ArrayList<Song> queue, int startPosition, boolean startPlaying) {
        if (mService != null) {
            //Log.e("Songs", "Now playing " + queue.size() + " songs");
            if (!tryToHandleOpenPlayingQueue(queue, startPosition, startPlaying)) {
                mService.openQueue(queue, startPosition, startPlaying);
            }
        }
    }

    private static boolean tryToHandleOpenPlayingQueue(ArrayList<Song> queue, int startPosition, boolean startPlaying) {
        if (getPlayingQueue().equals(queue)) {
            //Log.e("Songs", "Song list is the same");
            if (startPlaying) {
                if(mService != null && mService.isPlaying() && mService.getCurrentSong() == queue.get(startPosition)){
                    //Do nothing, the song is already playing
                }
                else{
                    playSongAt(startPosition);
                }
            }
            else {
                setPosition(startPosition);
            }

            return true;
        }

        return false;
    }

    /*
    public static boolean playNext(ArrayList<Song> songs){
        if (mService != null) {
            if (playingQueue.size > 0) {
                mService.addSongs(position + 1, songs);
            }
            else {
                openQueue(songs, 0, false);
            }

            String message = makeLabel(mService, R.plurals.SongsAddedToQueue, 1);
            Toast.makeText(mService, message, Toast.LENGTH_SHORT).show();

            return true;
        }
        return false;
    }

    public static boolean playNext(Song song){
        if (mService != null) {
            if (playingQueue.size > 0) {
                mService.addSong(position + 1, song);
            }
            else {
                ArrayList<Song> queue = new ArrayList<Song>();
                queue.add(song);
                openQueue(queue, 0, false);
            }

            String message = makeLabel(mService, R.plurals.SongsAddedToQueue, 1);
            Toast.makeText(mService, message, Toast.LENGTH_SHORT).show();

            return true;
        }

        return false;
    }*/

    public static void seek(final long position) {
        if (mService != null) {
            mService.seek((int) position);
        }
    }

    public static long position() {
        if (mService != null) {
            return mService.getPosition();
        }

        return 0;
    }

    public static void setPosition(int position) {
        if (mService != null) {
            mService.position = position;
        }
    }

    public static long duration() {
        if (mService != null) {
            return mService.getSongDurationMillis();
        }

        return -1;
    }

    public static int songProgressMillis(){
        if (mService != null) {
            return mService.getSongProgressMillis();
        }

        return -1;
    }

    public static int songDurationMillis(){
        if (mService != null) {
            return mService.getSongDurationMillis();
        }

        return -1;
    }

    public static boolean clearQueue() {
        if (mService != null) {
            mService.clearQueue();
            return true;
        }

        return false;
    }

    public static void playFromUri(Uri uri) {
        if (mService != null) {
            ArrayList<Song> songs = null;
            if (uri.getScheme() != null && uri.getAuthority() != null) {
                if (uri.getScheme() == ContentResolver.SCHEME_CONTENT) {
                    String songId = null;
                    if (uri.getAuthority() == "com.android.providers.media.documents") {
                        songId = getSongIdFromMediaProvider(uri);
                    }
                    else if (uri.getAuthority() == "media") {
                        songId = uri.getLastPathSegment();
                    }

                    if (songId != null) {
                        songs = SongLoader.getSongs(
                                mService,
                                MediaStore.Audio.AudioColumns._ID + "=?",
                                new String[] { songId  },
                                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                    }
                }
            }

            if (songs == null) {
                File songFile = null;
                if (uri.getAuthority() != null && uri.getAuthority() == "com.android.externalstorage.documents") {
                    songFile = new File(Environment.getExternalStorageDirectory(), uri.getPath().split(":", 2)[1]);
                }

                if (songFile == null) {
                    String path = getFilePathFromUri(mService, uri);
                    if (path != null){
                        songFile = new File(path);
                    }
                }

                if (songFile == null && uri.getPath() != null) {
                    songFile = new File(uri.getPath());
                }

                if (songFile != null) {
                    songs = SongLoader.getSongs(
                            mService,
                            MediaStore.Audio.AudioColumns.DATA + "=?",
                            new String[] { songFile.getAbsolutePath() },
                            MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                }
            }

            if (songs != null && !songs.isEmpty()) {
                openQueue(songs, 0, true);
            }
            else {
                //TODO the file is not listed in the media store
            }
        }
    }

    private static String getSongIdFromMediaProvider(Uri uri){
        return DocumentsContract.getDocumentId(uri).split(":")[1];
    }

    private static String getFilePathFromUri(Context context, Uri uri){

        String projection[] = { "_data" };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);;

        if(cursor != null){
            try{
                if(cursor.moveToFirst()){
                    return cursor.getString(cursor.getColumnIndex("_data"));
                }
            }
            catch (Exception e){

            }
            finally {
                cursor.close();
            }
        }

        return null;
    }

    /*

    public static boolean enqueue(Song song){
        if (mService != null) {
            if (playingQueue.size > 0) {
                mService.addSong(song);
            }
            else {
                ArrayList<Song> queue = new ArrayList<Song>();
                queue.add(song);
                openQueue(queue, 0, false);
            }

            String message = makeLabel(mService, R.plurals.SongsAddedToQueue, 1);
            Toast.makeText(mService, message, Toast.LENGTH_SHORT).show();

            return true;
        }

        return false;
    }

    public static boolean enqueue(ArrayList<Song> songs){
        if (mService != null) {
            if (playingQueue.size > 0) {
                mService.addSongs(songs);
            } else {
                openQueue(songs, 0, false);
            }

            String message = makeLabel(mService, R.plurals.SongsAddedToQueue, songs.size());
            Toast.makeText(mService, message, Toast.LENGTH_SHORT).show();

            return true;
        }

        return false;
    }

    public static boolean removeFromQueue(Song song){
        if (mService != null) {
            mService.removeSong(song);

            return true;
        }

        return false;
    }

    public static boolean removeFromQueue(int position){
        if (mService != null && position >= 0 && position < playingQueue.size) {
            mService.removeSong(position);
            return true;
        }

        return false;
    }

    public static final String makeLabel(final Context context, final int pluralInt, final int number) {
        return context.getResources().getQuantityString(pluralInt, number, number);
    }

    public static void addToPlaylist(final Context context, final long[] ids, final long playlistid) {
        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = null;
        int size = ids.length;
        int base = 0;

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        String[] projection = new String[]{"max(" + "play_order" + ")",};

        try {
            cursor = resolver.query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                base = cursor.getInt(0) + 1;
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        int numinserted = 0;
        for (int offSet = 0; offSet < size; offSet += 1000) {
            makeInsertItems(ids, offSet, 1000, base);
            numinserted += resolver.bulkInsert(uri, mContentValuesCache);
        }

        String message = context.getResources().getQuantityString(R.plurals.SongsAddedToPlaylist, numinserted, numinserted);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeInsertItems(final long[] ids, final int offset, int len, final int base) {
        if (offset + len > ids.length) {
            len = ids.length - offset;
        }

        if (mContentValuesCache == null || mContentValuesCache.length != len) {
            mContentValuesCache = new ContentValues[len];
        }
        for (int i = 0; i < len; i++) {
            if (mContentValuesCache[i] == null) {
                mContentValuesCache[i] = new ContentValues();
            }
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, ids[offset + i]);
        }
    }

    public static final long createPlaylist(final Context context, final String name) {
        if (name != null && name.length() > 0) {
            ContentResolver resolver = context.getContentResolver();

            Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            String[] projection = new String[]{MediaStore.Audio.PlaylistsColumns.NAME};
            String selection = MediaStore.Audio.PlaylistsColumns.NAME + " = '" + name + "'";

            Cursor cursor = resolver.query(uri, projection, selection, null, null);

            if (cursor.getCount() <= 0) {
                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Audio.PlaylistsColumns.NAME, name);

                Uri insertUri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                return Long.parseLong(insertUri.getLastPathSegment());
            }

            if (cursor != null) {
                cursor.close();
                cursor = null;
            }

            return -1;
        }

        return -1;
    }*/

    public static final class ServiceBinder implements ServiceConnection {
        private ServiceConnection mCallback;

        public ServiceBinder(ServiceConnection callback) {
            mCallback = callback;
        }

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            //Log.e("Remote", "Servix Connected");
            mService = ((MusicPlayerService.MusicBinder) service).getService();
            if (mCallback != null) {
                mCallback.onServiceConnected(className, service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(className);
            }

            mService = null;
        }
    }

    public static final class ServiceToken {
        public ContextWrapper mWrappedContext;

        public ServiceToken(final ContextWrapper context) {
            mWrappedContext = context;
        }
    }

}
