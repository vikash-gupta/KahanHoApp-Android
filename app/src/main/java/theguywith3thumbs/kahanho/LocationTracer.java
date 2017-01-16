package theguywith3thumbs.kahanho;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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
            String provider = mLocationManager.getBestProvider(criteria, true);
            Logger.d(Constants.AppNameForLogging, "Provider found: " + provider);

            if(provider.equals(LocationManager.GPS_PROVIDER) || provider.equals(LocationManager.NETWORK_PROVIDER))
                mLocationManager.requestSingleUpdate(provider, mLocationListener, null);
            else
                NotifyError("NoLocationProviderPresent");
        }
        catch(SecurityException e)
        {
            Logger.e(Constants.AppNameForLogging,"Couldn't get location");
            Logger.e(Constants.AppNameForLogging,e.toString());
            NotifyError("NoLocationPermissionPresent");
        }
        catch(IllegalArgumentException e)
        {
            Logger.e(Constants.AppNameForLogging,"Couldn't get location");
            Logger.e(Constants.AppNameForLogging,e.toString());
            NotifyError("LocationProviderNullException");
        }
        catch(Exception e)
        {
            Logger.e(Constants.AppNameForLogging,"Couldn't get location");
            Logger.e(Constants.AppNameForLogging,e.toString());
            NotifyError("GetLocationGeneralException");
        }
    }

    private void NotifyError(String error)
    {
        Notifier notifier = new Notifier(activityContext);
        notifier.SendNotification("Location not found", "Enable location sharing from Android Settings");
        Tracker mTracker = ((AnalyticsApplication) activityContext.getApplicationContext()).getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("MissedCall")
                .setAction(error)
                .build());
        Logger.e(Constants.AppNameForLogging, "Location not found");
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
        if(BackgroundService.isMissedCallProcessed)
            return;

        BackgroundService.isMissedCallProcessed = true;

        double lat = location.getLatitude();
        double lon = location.getLongitude();
        Logger.i(Constants.AppNameForLogging,"Latitude= " + String.valueOf(lat));
        Logger.i(Constants.AppNameForLogging,"Longitude= " + String.valueOf(lon));

        ReverseGeocoder r = new ReverseGeocoder(activityContext);
        r.Geocode(lat,lon);
    }
}
