package za.co.rationalthinkers.unoplayer.android.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.activity.MusicPlayerActivity;
import za.co.rationalthinkers.unoplayer.android.config.Constants;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.service.MusicPlayerService;

import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_NOW_PLAYING;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_QUIT;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_REWIND;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_SKIP;
import static za.co.rationalthinkers.unoplayer.android.config.Constants.ACTION_TOGGLE_PAUSE;

public class MusicPlayerNotification {

    private static final int NOTIFY_MODE_BACKGROUND = 0;
    private static final int NOTIFY_MODE_FOREGROUND = 1;

    private int notifyMode = NOTIFY_MODE_BACKGROUND;

    private MusicPlayerService mService;
    private NotificationManager mNotificationManager;
    private boolean stopped = false;

    private Bitmap artwork = null;

    public MusicPlayerNotification(MusicPlayerService service){
        this.mService = service;
        mNotificationManager = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
    }

    @RequiresApi(26)
    private void createNotificationChannel() {
        String name = "Uno Player";
        String description = "This notification is for showing the current playing music info and controls";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel notificationChannel = mNotificationManager.getNotificationChannel(Constants.NOTIFICATIONS_CHANNEL_ID_MUSIC_PLAYER);
        if(notificationChannel == null){
            notificationChannel = new NotificationChannel(Constants.NOTIFICATIONS_CHANNEL_ID_MUSIC_PLAYER, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }

    public void update(){
        stopped = false;

        Notification notification = buildNotification();
        if(stopped){
            // notification has been stopped before loading was finished
            return;
        }

        updateNotifyModeAndPostNotification(notification);
    }

    public void stop(){
        stopped = true;
        mService.stopForeground(true);
        mNotificationManager.cancel(Constants.NOTIFICATIONS_ID_MUSIC_PLAYER);
    }

    private Notification buildNotification() {

        Song song = mService.getCurrentSong();
        boolean isPlaying = mService.isPlaying();

        String songName = (!TextUtils.isEmpty(song.getTitle())) ? song.getTitle() : "Unknown";
        String albumName = (!TextUtils.isEmpty(song.getAlbumTitle())) ? song.getAlbumTitle() : "Unknown";
        String artistName = (!TextUtils.isEmpty(song.getArtistName())) ? song.getArtistName() : "Unknown";

        int playButtonResId = isPlaying ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp;
        int previousButtonResId = R.drawable.ic_skip_previous_white_24dp;
        int nextButtonResId = R.drawable.ic_skip_next_white_24dp;

        // -- Start Notif layout setup --
        RemoteViews notificationLayout = new RemoteViews(mService.getPackageName(), R.layout.notification_music_player);
        RemoteViews notificationLayoutBig = new RemoteViews(mService.getPackageName(), R.layout.notification_music_player_big);

        notificationLayout.setTextViewText(R.id.title, songName);
        notificationLayout.setTextViewText(R.id.text, artistName);
        notificationLayout.setImageViewResource(R.id.action_play_pause, playButtonResId);

        notificationLayoutBig.setTextViewText(R.id.title, songName);
        notificationLayoutBig.setTextViewText(R.id.text, artistName);
        notificationLayoutBig.setTextViewText(R.id.text2, albumName);
        notificationLayoutBig.setImageViewResource(R.id.action_play_pause, playButtonResId);

        setUpNotificationsButtonsListeners(notificationLayout, notificationLayoutBig);

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(playButtonResId, "Play/Pause", retrievePlaybackAction(ACTION_TOGGLE_PAUSE));
        NotificationCompat.Action previousAction = new NotificationCompat.Action(previousButtonResId, "Previous", retrievePlaybackAction(ACTION_REWIND));
        NotificationCompat.Action nextAction = new NotificationCompat.Action(nextButtonResId, "Next", retrievePlaybackAction(ACTION_SKIP));
        // -- End Notif layout setup --

        Intent nowPlayingIntent = getNowPlayingIntent(mService);
        PendingIntent clickIntent = PendingIntent.getActivity(mService, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deleteIntent = buildPendingIntent(mService, ACTION_QUIT, null);

        //Album artwork
        loadAlbumArtwork("");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mService, Constants.NOTIFICATIONS_CHANNEL_ID_MUSIC_PLAYER)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(mService, R.color.colorAccent))
                .setContentIntent(clickIntent)
                .setDeleteIntent(deleteIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContent(notificationLayout)
                .setCustomBigContentView(notificationLayoutBig)
                .setOngoing(isPlaying)
                //.setWhen(System.currentTimeMillis())
                .addAction(previousAction)
                .addAction(playPauseAction)
                .addAction(nextAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);

            MediaStyle style = new MediaStyle()
                    .setMediaSession(mService.getMediaSessionToken())
                    .setShowActionsInCompactView(0, 1, 2);

            builder.setStyle(style);
        }

        if (artwork != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(Palette.from(artwork).generate().getVibrantColor(Color.parseColor("#403f4d")));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setColorized(true);
        }

        return builder.build();
    }

    private void updateNotifyModeAndPostNotification(Notification notification) {
        int newNotifyMode = mService.isPlaying() ? NOTIFY_MODE_FOREGROUND : NOTIFY_MODE_BACKGROUND;

        if (notifyMode != newNotifyMode && newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            mService.stopForeground(false);
        }

        if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
            mService.startForeground(Constants.NOTIFICATIONS_ID_MUSIC_PLAYER, notification);
        }
        else if (newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            mNotificationManager.notify(Constants.NOTIFICATIONS_ID_MUSIC_PLAYER, notification);
        }

        notifyMode = newNotifyMode;
    }

    private void setUpNotificationsButtonsListeners(RemoteViews notificationLayout, RemoteViews notificationLayoutBig){
        PendingIntent pendingIntent;

        ComponentName serviceName = new ComponentName(mService, MusicPlayerService.class);

        // Previous track
        pendingIntent = buildPendingIntent(mService, ACTION_REWIND, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_prev, pendingIntent);
        notificationLayoutBig.setOnClickPendingIntent(R.id.action_prev, pendingIntent);

        // Play and pause
        pendingIntent = buildPendingIntent(mService, ACTION_TOGGLE_PAUSE, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_play_pause, pendingIntent);
        notificationLayoutBig.setOnClickPendingIntent(R.id.action_play_pause, pendingIntent);

        // Next track
        pendingIntent = buildPendingIntent(mService, ACTION_SKIP, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.action_next, pendingIntent);
        notificationLayoutBig.setOnClickPendingIntent(R.id.action_next, pendingIntent);

        // Close
        pendingIntent = buildPendingIntent(mService, ACTION_QUIT, serviceName);
        notificationLayoutBig.setOnClickPendingIntent(R.id.action_quit, pendingIntent);
    }

    private Intent getNowPlayingIntent(Context context) {
        Intent intent = new Intent(context, MusicPlayerActivity.class);
        intent.setAction(ACTION_NOW_PLAYING);
        return intent;
    }

    private PendingIntent buildPendingIntent(Context context, String action, ComponentName serviceName){
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);

        return PendingIntent.getService(context, 0, intent, 0);
    }

    private PendingIntent retrievePlaybackAction(final String action) {
        final ComponentName serviceName = new ComponentName(mService, MusicPlayerService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(mService, 0, intent, 0);
    }

    private void loadAlbumArtwork(String path){
        try {
            FutureTarget<Bitmap> futureBitmap = Glide.with(mService)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_music_note_white_24dp)
                            .error(R.drawable.ic_music_note_white_24dp)
                    )
                    .asBitmap()
                    .load(path)
                    .submit();
            artwork = futureBitmap.get();
        }
        catch (Exception e){

        }
    }

}
