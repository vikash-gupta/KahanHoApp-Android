package theguywith3thumbs.kahanho;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by home on 28/10/15.
 */
public class LocationTracer {
    private Context activityContext;

    public LocationTracer(Context context)
    {
        activityContext = context;
    }
    public void GetLocation()
    {
        Log.i(Constants.AppNameForLogging, "requesting for updates");
        LocationManager mLocationManager = (LocationManager) activityContext.getSystemService(
                activityContext.LOCATION_SERVICE);
        try {
            Criteria criteria = new Criteria();
            String provider = mLocationManager.getBestProvider(criteria, false);
            Log.i(Constants.AppNameForLogging, "Getting location from " + provider + " provider");
            /*Location location = mLocationManager.getLastKnownLocation(provider);
            if (location != null) {
                Log.i(Constants.AppNameForLogging, "Got last location");
                UploadLocation(location);
            }
            else
            {
                Log.i(Constants.AppNameForLogging,"location is null");
            }*/
            mLocationManager.requestLocationUpdates(provider, 2, 1, mLocationListener);
        }
        catch(SecurityException e)
        {
            Log.e(Constants.AppNameForLogging,"Couldn't get location");
            Log.e(Constants.AppNameForLogging,e.toString());
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            Log.i(Constants.AppNameForLogging,"onLocationChanged");
            UploadLocation(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void UploadLocation(Location location)
    {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        Log.i(Constants.AppNameForLogging,"Latitude= " + String.valueOf(lat));
        Log.i(Constants.AppNameForLogging,"Longitude= " + String.valueOf(lon));
        LocationUploader o = new LocationUploader(activityContext);
        o.Upload(lat,lon);
    }
}
