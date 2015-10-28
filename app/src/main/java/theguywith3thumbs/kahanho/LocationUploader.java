package theguywith3thumbs.kahanho;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by home on 28/10/15.
 */
public class LocationUploader {

    private String URL;
    private Context activityContext;
    public LocationUploader(Context context)
    {
        activityContext = context;
        URL = "http://data.sparkfun.com/input/";
    }
    public void Upload(double latitude, double longitude)
    {
        String dataUrl = URL + String.valueOf(latitude) + "," + String.valueOf(longitude);
        try {
            Log.i(Constants.AppNameForLogging,"Trying to upload data");
            sendRequest(dataUrl);
        }
        catch(Exception e)
        {
            Log.e(Constants.AppNameForLogging,"Couldn't upload data");
            Log.e(Constants.AppNameForLogging,e.toString());
        }
    }

    private void sendRequest(String url)
    {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(activityContext);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(Constants.AppNameForLogging,"Location uploaded, reponse follows");
                        Log.i(Constants.AppNameForLogging,response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.AppNameForLogging,"Couldn't upload data due to below error");
                Log.e(Constants.AppNameForLogging,error.toString());
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void sendGET(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } else {
            Log.e(Constants.AppNameForLogging, "Couldn't upload data");
        }

    }
}
