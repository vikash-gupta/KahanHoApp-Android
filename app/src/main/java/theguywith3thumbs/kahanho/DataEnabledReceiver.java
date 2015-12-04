package theguywith3thumbs.kahanho;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by home on 1/12/15.
 */
public class DataEnabledReceiver extends BroadcastReceiver {

    // http://stackoverflow.com/questions/27559837/how-to-trigger-broadcastreceiver-when-i-turn-on-off-mobile-cellular-datamobile
    @Override
    public void onReceive(Context context, Intent intent) {

        if(!MobileDataState.WasTurnedOn)
            return ;


        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {


            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (!noConnectivity) {
                Log.d(Constants.AppNameForLogging, "Mobile Data turned on");
                LocationTracer tracer = new LocationTracer(context);
                tracer.GetLocation();
            }
        }

        /*ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        boolean dataEnabled = isConnected && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;

        if(dataEnabled)
        {
            //LocationTracer tracer = new LocationTracer(context);
            //tracer.GetLocation();
            //if(mobileDataWasTurnedOn)
              //  enabler.toggleMobileData(activityContext,false);
        }*/

    }
}
