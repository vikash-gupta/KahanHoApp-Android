package theguywith3thumbs.kahanho;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by home on 25/10/15.
 */
public class MobileServiceEnabler {

    public void toggleMobileData(Context context, boolean enabled) {

        /*if(Build.VERSION.SDK_INT >19) {
            setMobileDataState(enabled, context);
            return;
        }*/

        final ConnectivityManager conman =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(
                    iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (Exception e) {
            Logger.e(Constants.AppNameForLogging,e.toString());
            Tracker mTracker = ((AnalyticsApplication) context.getApplicationContext()).getDefaultTracker();
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("MissedCall")
                    .setAction("Error:DataDisabled")
                    .build());
            //Notifier notifier = new Notifier(context);
            //notifier.SendNotification("Location not found", "Enable Data from Android Settings");
            LocationTracer tracer = new LocationTracer(context);
            tracer.GetLocation();
        }
    }

    public boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*private void toggleWifi(Context context, boolean status)
    {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(status);
    }



    public void setMobileDataState(boolean mobileDataEnabled, Context context)
    {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

            if (null != setMobileDataEnabledMethod)
            {
                setMobileDataEnabledMethod.invoke(telephonyService, mobileDataEnabled);
            }
        }
        catch (Exception ex)
        {
            Logger.e(Constants.AppNameForLogging, "Error setting mobile data state" + ex.getLocalizedMessage());
        }
    }*/
}
