package theguywith3thumbs.kahanho;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


public class BackgroundService extends Service {

    InComingCallListener phoneListener;
    private final IBinder mBinder = new MyBinder();
    TelephonyManager telephony;
    static String number;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String n = intent.getStringExtra(Constants.CallerNumber);


        Logger.i(Constants.AppNameForLogging, "service called with tracker-" + n);
        if(n!=null && !n.equalsIgnoreCase(number)) { // considering re-entry
            telephony = (TelephonyManager) this
                    .getSystemService(Context.TELEPHONY_SERVICE);
            //telephony.listen(null, PhoneStateListener.LISTEN_NONE);
            number = Caller.number = n;
            Logger.i(Constants.AppNameForLogging, Caller.number);
            phoneListener = new InComingCallListener(this);
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        return Service.START_REDELIVER_INTENT;
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

    public String getTracker()
    {
        return number;
    }
    @Override
    public void onDestroy ()
    {
        telephony.listen(null, PhoneStateListener.LISTEN_NONE);
        number = Caller.number = null;
    }
}
