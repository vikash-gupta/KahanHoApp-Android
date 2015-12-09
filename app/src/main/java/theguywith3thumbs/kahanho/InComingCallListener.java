package theguywith3thumbs.kahanho;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


public class InComingCallListener extends PhoneStateListener {

    private Context activityContext;
    Toast toast = null;
    boolean ring = false;
    boolean callReceived = false;

    public InComingCallListener(Context context)
    {
        activityContext = context;
    }

    public void onCallStateChanged(int state, String incomingNumber) {

        //
        //Log.i(Constants.AppNameForLogging, "onCallStateChanged");
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                if (CallMissed() && Caller.number.equalsIgnoreCase(incomingNumber))
                {
                    Log.i(Constants.AppNameForLogging, "Call missed from tracker number " + incomingNumber);
                    //String msg = Caller.number + " is calling";
                    //toast = Toast.makeText(activityContext, msg, Toast.LENGTH_SHORT);
                    //toast.show();
                    MobileServiceEnabler enabler = new MobileServiceEnabler();
                    if (enabler.isNetworkAvailable(activityContext))
                    {
                        Log.i(Constants.AppNameForLogging, "Internet connection is on");
                        LocationTracer tracer = new LocationTracer(activityContext);
                        tracer.GetLocation();
                    }
                    else
                    {
                        Log.i(Constants.AppNameForLogging, "No internet connection, turning mobile data on...");
                        MobileDataState.WasTurnedOn = true;
                        enabler.toggleMobileData(activityContext,true);
                        /*activityContext.registerReceiver(
                                new DataEnabledReceiver(),
                                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));*/
                    }
                }
                //Log.i(Constants.AppNameForLogging, "idle" + "ring=" + ring + " callreceived= " + callReceived);
                ring = callReceived = false;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Log.i(Constants.AppNameForLogging, "CALL_STATE_OFFHOOK");
                callReceived = true;

                break;
            case TelephonyManager.CALL_STATE_RINGING:
                //Log.i(Constants.AppNameForLogging, "CALL_STATE_RINGING");
                ring = true;

                break;
        }
    }

    private boolean CallMissed() {
        return ring && !callReceived;
    }

}

