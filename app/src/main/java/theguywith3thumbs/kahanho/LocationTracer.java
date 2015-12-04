package theguywith3thumbs.kahanho;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

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

        LocationManager mLocationManager = (LocationManager) activityContext.getSystemService(
                Context.LOCATION_SERVICE);
        try {
            Criteria criteria = new Criteria();
            String provider = mLocationManager.getBestProvider(criteria, false);
            Log.d(Constants.AppNameForLogging, "Getting location from " + provider + " provider");
            
            mLocationManager.requestSingleUpdate(provider, mLocationListener, null);
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

        ReverseGeocoder r = new ReverseGeocoder(activityContext);
        r.Geocode(lat,lon);
    }
}
