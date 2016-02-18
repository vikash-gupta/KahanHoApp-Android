package theguywith3thumbs.kahanho;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReverseGeocoder {
    private Context activityContext;
    private String baseURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private Notifier notifier;
    private Tracker mTracker;

    public ReverseGeocoder(Context context)
    {
        activityContext = context;
        mTracker = ((AnalyticsApplication) activityContext.getApplicationContext()).getDefaultTracker();
    }

    public void Geocode(double lat, double lon)
    {
        baseURL += String.valueOf(lat) + ',' + String.valueOf(lon);
        Logger.i(Constants.AppNameForLogging, "inside reverse geocoding");
        notifier = new Notifier(activityContext);
        sendRequest();
    }

    private void sendRequest()
    {
        RequestQueue queue = Volley.newRequestQueue(activityContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.d(Constants.AppNameForLogging, "Got reverse geocoding response");
                        //Logger.v(Constants.AppNameForLogging, response);
                        SendMessage(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e(Constants.AppNameForLogging,"Couldn't send request due to below error");
                Logger.e(Constants.AppNameForLogging,error.toString());

                SendLatLonMsg();
            }
        });

        queue.add(stringRequest);
    }

    private void SendLatLonMsg()
    {
        String msg = "Couldn't get street address, follow link to get my current location " + baseURL;
        SendSms(msg);
        notifier.SendNotification("Location not sent", "Reverse geo-coding error");
    }
    private void SendSms(String msg)
    {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(BackgroundService.number, null, msg, null, null);

        if(MobileDataState.WasTurnedOn)
        {
            MobileServiceEnabler enabler = new MobileServiceEnabler();
            enabler.toggleMobileData(activityContext,false);
        }

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("MissedCall")
                .setAction("SMSSent")
                .build());

    }


    private void SendMessage(String location)
    {
        String address = null;
        try {
            JSONObject response = new JSONObject(location);
            JSONArray results = response.getJSONArray("results");
            JSONObject result = results.getJSONObject(0);
            address = result.getString("formatted_address");
        } catch (JSONException e) {
            //e.printStackTrace();
            Logger.e(Constants.AppNameForLogging,e.getMessage());
        }

        if(address == null)
        {
            SendLatLonMsg();
            return;
        }

        Logger.i(Constants.AppNameForLogging, "Sending current location: " + address + " to " + BackgroundService.number);

        SendSms("My current location is " + address);
        notifier.SendNotification("Location sent to " + BackgroundService.number,address);

    }
}
