package za.co.rationalthinkers.unoplayer.android.config;

import android.content.Context;
import android.content.SharedPreferences;

import za.co.rationalthinkers.unoplayer.android.BuildConfig;

public class Settings {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Settings(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.SettingsFile, Context.MODE_PRIVATE);
    }

    public boolean isFirstRun(){
        return sharedPreferences.getBoolean(Constants.SHARED_PREFERENCES_FIRST_RUN, true);
    }

    public int getLastUpdatedVersion(){
        return sharedPreferences.getInt(Constants.SHARED_PREFERENCES_CURRENT_VERSION, 0);
    }

    public int getCurrentVersion(){
        return BuildConfig.VERSION_CODE;
    }

    public String getUserName(){
        return sharedPreferences.getString(Constants.SHARED_PREFERENCES_USER_NAME, "");
    }

    public String getUserPhoto(){
        return sharedPreferences.getString(Constants.SHARED_PREFERENCES_USER_PHOTO, "");
    }

    public long getLastConnectionTime(){
        return sharedPreferences.getLong(Constants.SHARED_PREFERENCES_LAST_CONNECTION_TIME, 0L);
    }

    public long getLastDisconnectionTime(){
        return sharedPreferences.getLong(Constants.SHARED_PREFERENCES_LAST_DISCONNECTION_TIME, 0L);
    }

    public int getRepeatMode(){
        return sharedPreferences.getInt(Constants.SHARED_PREFERENCES_REPEAT_MODE, RepeatMode.REPEAT_OFF);
    }

    public int getShuffleMode(){
        return sharedPreferences.getInt(Constants.SHARED_PREFERENCES_SHUFFLE_MODE, ShuffleMode.SHUFFLE_OFF);
    }

    public void setFirstRun(boolean opened){
        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.SHARED_PREFERENCES_FIRST_RUN, opened);
        editor.apply();
    }

    public void setLastUpdatedVersion(int version){
        editor = sharedPreferences.edit();
        editor.putInt(Constants.SHARED_PREFERENCES_CURRENT_VERSION, version);
        editor.apply();
    }

    public void setUserName(String name){
        editor = sharedPreferences.edit();
        editor.putString(Constants.SHARED_PREFERENCES_USER_NAME, name);
        editor.apply();
    }

    public void setUserPhoto(String photo){
        editor = sharedPreferences.edit();
        editor.putString(Constants.SHARED_PREFERENCES_USER_PHOTO, photo);
        editor.apply();
    }

    public void setLastConnectionTime(long time){
        editor = sharedPreferences.edit();
        editor.putLong(Constants.SHARED_PREFERENCES_LAST_CONNECTION_TIME, time);
        editor.apply();
    }

    public void setLastDisconnectionTime(long time){
        editor = sharedPreferences.edit();
        editor.putLong(Constants.SHARED_PREFERENCES_LAST_DISCONNECTION_TIME, time);
        editor.apply();
    }

    public void setSetRepeatMode(int mode){
        editor = sharedPreferences.edit();
        editor.putInt(Constants.SHARED_PREFERENCES_REPEAT_MODE, mode);
        editor.apply();
    }

    public void setSetShuffleMode(int mode){
        editor = sharedPreferences.edit();
        editor.putInt(Constants.SHARED_PREFERENCES_SHUFFLE_MODE, mode);
        editor.apply();
    }

}
