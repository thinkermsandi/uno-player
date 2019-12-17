package za.co.rationalthinkers.unoplayer.android.config;

import za.co.rationalthinkers.unoplayer.android.BuildConfig;

public class Constants {

    public static String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static String MUSIC_PACKAGE_NAME = "com.android.music";

    public static String AuthFile = PACKAGE_NAME + ".USER_ACCOUNT_DETAILS";
    public static String SettingsFile = PACKAGE_NAME + ".USER_ACCOUNT_DETAILS";

    public static String SHARED_PREFERENCES_FIRST_RUN = "first_run";
    public static String SHARED_PREFERENCES_CURRENT_VERSION = "current_version";
    public static String SHARED_PREFERENCES_USER_NAME = "name";
    public static String SHARED_PREFERENCES_USER_PHOTO = "photo";
    public static String SHARED_PREFERENCES_LAST_CONNECTION_TIME = "last_connected";
    public static String SHARED_PREFERENCES_LAST_DISCONNECTION_TIME = "last_disconnected";
    public static String SHARED_PREFERENCES_REPEAT_MODE = "repeat_mode";
    public static String SHARED_PREFERENCES_SHUFFLE_MODE = "shuffle_mode";

    public static final String ARG_ALBUM = "album";
    public static final String ARG_ARTIST = "artist";
    public static final String ARG_PLAYLIST = "playlist";
    public static final String ARG_SONGS = "songs";
    public static final String ARG_SONG = "song";
    public static final String ARG_SONG_PATH = "song_path";
    public static final String ARG_VIDEOS = "videos";
    public static final String ARG_VIDEO = "video";
    public static final String ARG_VIDEO_PATH = "video_path";

    public static final String ACTION_TOGGLE_PAUSE = PACKAGE_NAME + ".togglepause";
    public static final String ACTION_PLAY = PACKAGE_NAME + ".play";
    public static final String ACTION_NOW_PLAYING = PACKAGE_NAME + ".nowplaying";
    public static final String ACTION_PLAY_PLAYLIST = PACKAGE_NAME + ".play.playlist";
    public static final String ACTION_PAUSE = PACKAGE_NAME + ".pause";
    public static final String ACTION_STOP = PACKAGE_NAME + ".stop";
    public static final String ACTION_SKIP = PACKAGE_NAME + ".skip";
    public static final String ACTION_REWIND = PACKAGE_NAME + ".rewind";
    public static final String ACTION_QUIT = PACKAGE_NAME + ".quitservice";
    public static final String INTENT_EXTRA_PLAYLIST = PACKAGE_NAME + "intentextra.playlist";
    public static final String INTENT_EXTRA_SHUFFLE_MODE = PACKAGE_NAME + ".intentextra.shufflemode";

    public static final String META_CHANGED = PACKAGE_NAME + ".metachanged";
    public static final String QUEUE_CHANGED = PACKAGE_NAME + ".queuechanged";
    public static final String PLAY_STATE_CHANGED = PACKAGE_NAME + ".playstatechanged";
    public static final String REPEAT_MODE_CHANGED = PACKAGE_NAME + ".repeatmodechanged";
    public static final String SHUFFLE_MODE_CHANGED = PACKAGE_NAME + ".shufflemodechanged";
    public static final String MEDIA_STORE_CHANGED = PACKAGE_NAME + ".mediastorechanged";

    public static String THREAD_HANDLER = "THREAD_HANDLER";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "unoshare.db";
    public static final String DATABASE_TABLE_NAME_BLOCK_LIST = "block_list";

    public static final String INTENT_ACTION_FILE_TRANSFER_UPDATE = "za.co.rationalthinkers.unomessenger:FILE_TRANSFER_STATUS_CHANGED";
    public static final String INTENT_ACTION_HOTSPOT_STATUS_CHANGED = "za.co.rationalthinkers.unomessenger:HOTSPOT_STATUS_CHANGED";

    public static final String NOTIFICATIONS_CHANNEL_ID_MUSIC_PLAYER = "playing_notification";

    public static final int NOTIFICATIONS_ID_MUSIC_PLAYER = 1;

    public static final String TAG_MEDIA_SESSION = "media_session";
    public static final String TAG_WAKELOCK_HEADSET_BUTTON = PACKAGE_NAME + ":wakelock_headset_button";
    public static final String TAG_FRAGMENT_ALBUM_DETAILS = "albums_details_fragment";
    public static final String TAG_FRAGMENT_ALBUMS = "albums_fragment";
    public static final String TAG_FRAGMENT_ARTIST_DETAILS = "artist_details_fragment";
    public static final String TAG_FRAGMENT_ARTIST = "artist_fragment";
    public static final String TAG_FRAGMENT_AUDIO_PLAYER = "audio_player_fragment";
    public static final String TAG_FRAGMENT_MUSIC = "music_fragment";
    public static final String TAG_FRAGMENT_PLAYLIST_DETAILS = "playlist_details_fragment";
    public static final String TAG_FRAGMENT_PLAYLIST_ADD = "playlist_add_fragment";
    public static final String TAG_FRAGMENT_PLAYLISTS= "playlists_fragment";
    public static final String TAG_FRAGMENT_SETTINGS = "settings_fragment";
    public static final String TAG_FRAGMENT_SONGS = "songs_fragment";
    public static final String TAG_FRAGMENT_VIDEO_PLAYER = "video_player_fragment";
    public static final String TAG_FRAGMENT_VIDEOS = "videos_fragment";
    public static final String TAG_WAKE_LOCK_RECEIVER_SERVICE = "za.co.rationalthinkers.unomessenger:TAG_WAKE_LOCK_RECEIVER_SERVICE";
    public static final String TAG_WAKE_LOCK_SENDER_SERVICE = "za.co.rationalthinkers.unomessenger:TAG_WAKE_LOCK_SENDER_SERVICE";

    public static String NOTIFICATIONS_CHANNEL = "NOTIFICATION_CH_1";

    public static int API_ERROR_AUTHENTICATION = 401;
    public static int API_ERROR_SUBSCRIPTION = 402;
    public static int API_ERROR_PRIVILEGES = 403;
    public static int API_ERROR_RESOURCE_NOT_FOUND = 404;
    public static int API_ERROR_TIMEOUT = 408;

    public static final int PERMISSIONS_REQUEST_CODE = 1000;
    public static final int FIREBASE_AUTH_REQUEST_CODE = 1001;
    public static final int PLAY_SERVICES_REQUEST_CODE = 1002;
    public static final int IMAGE_CAPTURE_REQUEST_CODE = 1003;
    public static final int DEFAULT_SMS_APP_REQUEST_CODE = 1004;

    public static final int AUTH_ERROR_AUTHENTICATOR_FAIL = 1;
    public static final int AUTH_ERROR_OPERATION_CANCELLED = 2;
    public static final int AUTH_ERROR_IO_PROBLEM = 3;

    public static int MAX_FILES_PER_TRANSFER = 20;

    public static final int DELAY_SPLASHSCREEN = 2000;

}
