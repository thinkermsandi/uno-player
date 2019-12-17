package za.co.rationalthinkers.unoplayer.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.activity.AboutActivity;
import za.co.rationalthinkers.unoplayer.android.activity.MusicActivity;
import za.co.rationalthinkers.unoplayer.android.activity.PlaylistActivity;
import za.co.rationalthinkers.unoplayer.android.activity.SettingsActivity;
import za.co.rationalthinkers.unoplayer.android.activity.VideoActivity;
import za.co.rationalthinkers.unoplayer.android.model.Playlist;

public class NavigationUtils {

    public static final void openMusicActivity(final Activity activity, boolean finishActivity) {
        if(activity == null){
            return;
        }

        Context context = activity.getApplicationContext();

        Intent musicIntent = new Intent(context, MusicActivity.class);
        context.startActivity(musicIntent);

        if(finishActivity){
            activity.finish();
        }
    }

    public static final void openVideosActivity(final Activity activity, boolean finishActivity) {
        if(activity == null){
            return;
        }

        Context context = activity.getApplicationContext();

        Intent musicIntent = new Intent(context, VideoActivity.class);
        context.startActivity(musicIntent);

        if(finishActivity){
            activity.finish();
        }
    }

    public static final void openPlaylistsActivity(final Activity activity, boolean finishActivity) {
        if(activity == null){
            return;
        }

        Context context = activity.getApplicationContext();

        Intent musicIntent = new Intent(context, PlaylistActivity.class);
        context.startActivity(musicIntent);

        if(finishActivity){
            activity.finish();
        }
    }

    public static final void openSettingsActivity(final Activity activity, boolean finishActivity) {
        if(activity == null){
            return;
        }

        Context context = activity.getApplicationContext();

        Intent musicIntent = new Intent(context, SettingsActivity.class);
        context.startActivity(musicIntent);

        if(finishActivity){
            activity.finish();
        }
    }

    public static final void openAboutActivity(final Activity activity, boolean finishActivity) {
        if(activity == null){
            return;
        }

        Context context = activity.getApplicationContext();

        Intent musicIntent = new Intent(context, AboutActivity.class);
        context.startActivity(musicIntent);

        if(finishActivity){
            activity.finish();
        }
    }

}
