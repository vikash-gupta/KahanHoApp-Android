package theguywith3thumbs.kahanho;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReverseGeocoder {
    private Context activityContext;
    private String baseURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";

    public ReverseGeocoder(Context context)
    {
        activityContext = context;
    }

    public void Geocode(double lat, double lon)
    {
        baseURL += String.valueOf(lat) + ',' + String.valueOf(lon);
        sendRequest();
    }

    private void sendRequest()
    {
        RequestQueue queue = Volley.newRequestQueue(activityContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(Constants.AppNameForLogging, "Got reverse geocoding response");
                        Log.v(Constants.AppNameForLogging, response);
                        SendMessage(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.AppNameForLogging,"Couldn't send request due to below error");
                Log.e(Constants.AppNameForLogging,error.toString());

                SendLatLonMsg();
            }
        });

        queue.add(stringRequest);
    }

    private void SendLatLonMsg()
    {
        String msg = "Couldn't get street address, follow link to get my current location " + baseURL;
        SendSms(msg);
    }
    private void SendSms(String msg)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(Caller.number, null, msg, null, null);

        if(MobileDataState.WasTurnedOn)
        {
            MobileServiceEnabler enabler = new MobileServiceEnabler();
            enabler.toggleMobileData(activityContext,false);
        }
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
            Log.e(Constants.AppNameForLogging,e.getMessage());
        }

        if(address == null)
        {
            SendLatLonMsg();
            return;
        }

        Log.i(Constants.AppNameForLogging, "Sending current location: " + address + " to " + Caller.number);

        SendSms("My current location is " + address);

    }
}
