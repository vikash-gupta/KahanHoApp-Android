package theguywith3thumbs.kahanho;

import android.util.Log;

/**
 * Created by home on 10/12/15.
 */
public class Logger {
    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.i(tag, msg);
    }
    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.e(tag, msg);
    }
    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.v(tag, msg);
    }
    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.d(tag, msg);
    }

}
