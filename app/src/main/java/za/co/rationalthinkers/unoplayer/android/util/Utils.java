package za.co.rationalthinkers.unoplayer.android.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import za.co.rationalthinkers.unoplayer.android.R;

public class Utils {

    public static final String makeShortTimeString(final Context context, long secs) {
        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources().getString(hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }

    public static String millisToString(long millis) {
        StringBuilder sb = new StringBuilder();
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);

        sb.setLength(0);
        if (millis < 0) {
            millis = -millis;
            sb.append("-");
        }

        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        String secStr = String.format(Locale.US, "%02d", sec);
        String minStr = String.format(Locale.US, "%02d", min);
        String hoursStr = String.format(Locale.US, "%02d", hours);

        if (hours > 0){
            sb.append(hours).append(':').append(minStr).append(':').append(secStr);
        }
        else{
            sb.append(minStr).append(':').append(secStr);
        }

        return sb.toString();
    }

    /** Changes the System Bar Theme. */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static final void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    /*
     * Convert file:// uri from real path to emulated FS path.
     */
    public static Uri convertLocalUri(Uri uri) {
        if (!TextUtils.equals(uri.getScheme(), "file") || !uri.getPath().startsWith("/sdcard")){
            return uri;
        }

        String directory = Environment.getExternalStorageDirectory().getPath();

        return Uri.parse(uri.toString().replace("/sdcard", directory));
    }

}
