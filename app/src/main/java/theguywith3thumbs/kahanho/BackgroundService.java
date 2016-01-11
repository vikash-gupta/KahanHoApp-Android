package theguywith3thumbs.kahanho;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


public class BackgroundService extends Service {

    InComingCallListener phoneListener;
    private final IBinder mBinder = new MyBinder();
    TelephonyManager telephony;
    static String number; // hack to check if service is running
    static boolean isMissedCallProcessed;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences settings = getSharedPreferences(Constants.SharedPreferencesFile, 0);
        String n = settings.getString(Constants.CallerNumber, null);

        if(n!=null && !n.equalsIgnoreCase(number) && phoneListener ==null) { // considering re-entry
            Logger.i(Constants.AppNameForLogging, "service called with tracker-" + n);
            telephony = (TelephonyManager) this
                    .getSystemService(Context.TELEPHONY_SERVICE);
            //telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);

            number = n;
            Logger.i(Constants.AppNameForLogging, number);
            phoneListener = new InComingCallListener(this);
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    @Override
    public void onDestroy ()
    {
        if(telephony!=null)
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
        number = null;
        phoneListener = null;
    }
}
