package theguywith3thumbs.kahanho;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by home on 25/10/15.
 */
public class InComingCallListener extends PhoneStateListener {

    private Context activityContext;
    Toast toast = null;
    public InComingCallListener(Context context)
    {
        activityContext = context;
    }

    public void onCallStateChanged(int state, String incomingNumber) {


        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                if(toast !=null)
                    toast.cancel();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if(toast !=null)
                    toast.cancel();
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i(Constants.AppNameForLogging, "incoming call " + incomingNumber);
                if(RegexCaller.number !=null)
                {
                    Pattern r = Pattern.compile(RegexCaller.number);

                    Matcher m = r.matcher(incomingNumber);
                    if (m.find()) {
                        String msg = RegexCaller.number + " is calling";
                        toast = Toast.makeText(activityContext, msg, Toast.LENGTH_SHORT);
                        toast.show();
                        MobileServiceEnabler enabler = new MobileServiceEnabler();
                        enabler.setMobileDataEnabled(activityContext, true);
                        LocationTracer tracer = new LocationTracer(activityContext);
                        tracer.GetLocation();
                    }
                }

                break;
        }
    }

}

