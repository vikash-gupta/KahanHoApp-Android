package theguywith3thumbs.kahanho;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;


public class InComingCallListener extends PhoneStateListener {

    private Context activityContext;
    boolean ring = false;
    boolean callReceived = false;

    public InComingCallListener(Context context)
    {
        activityContext = context;
        Logger.i(Constants.AppNameForLogging, "InComingCallListener");
    }

    public void onCallStateChanged(int state, String incomingNumber) {

        Logger.i(Constants.AppNameForLogging, "onCallStateChanged");
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                if (CallMissed() && BackgroundService.number.equalsIgnoreCase(incomingNumber))
                {
                    Logger.i(Constants.AppNameForLogging, "Call missed from tracker number " + incomingNumber);
                    BackgroundService.isMissedCallProcessed = false;
                    MobileServiceEnabler enabler = new MobileServiceEnabler();
                    if (enabler.isNetworkAvailable(activityContext))
                    {
                        Logger.i(Constants.AppNameForLogging, "Internet connection is on");
                        LocationTracer tracer = new LocationTracer(activityContext);
                        tracer.GetLocation();
                    }
                    else
                    {
                        Logger.i(Constants.AppNameForLogging, "No internet connection, turning mobile data on...");
                        MobileDataState.WasTurnedOn = true;
                        enabler.toggleMobileData(activityContext,true);
                        /*activityContext.registerReceiver(
                                new DataEnabledReceiver(),
                                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));*/
                    }
                }
                Logger.i(Constants.AppNameForLogging, "call missed = " + CallMissed() + "tracker =" + BackgroundService.number);
                ring = callReceived = false;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Logger.i(Constants.AppNameForLogging, "CALL_STATE_OFFHOOK");
                callReceived = true;

                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Logger.i(Constants.AppNameForLogging, "CALL_STATE_RINGING");
                ring = true;

                break;
        }
    }

    private boolean CallMissed() {
        return ring && !callReceived;
    }

}

