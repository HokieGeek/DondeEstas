package net.hokiegeek.android.dondeestas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by andres on 11/29/16.
 */

public class LocationPublisher
    implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener
{
    private static final String TAG = "DONDE";

    private GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;

    private Context context;

    public void init(Context parent) {
        context = parent;

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void start() {
        Log.v(TAG, "start()");
        googleApiClient.connect();
    }

    public void stop() {
        Log.v(TAG, "stop()");
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "onConnected()");
        // TODO: Verify location settings
        /*
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()) {
             @Override
             public void onResult(LocationSettingsResult result) {
                 final Status status = status.getStatus();
                 final LocationSettingsStates = result.getLocationSettingsStates();
                 switch (status.getStatusCode()) {
                     case LocationSettingsStatusCodes.SUCCESS:
                         // All location settings are satisfied. The client can
                         // initialize location requests here. ...
                         break;
                     case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                         // Location settings are not satisfied, but this can be fixed
                         // by showing the user a dialog.
                         try {
                             // Show the dialog by calling startResolutionForResult(),
                             // and check the result in onActivityResult().
                             status.startResolutionForResult(
                                 OuterClass.this,
                                 REQUEST_CHECK_SETTINGS);
                         } catch (IntentSender.SendIntentException e) {
                             // Ignore the error.
                         }
                         break;
                     case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                         // Location settings are not satisfied. However, we have no way
                         // to fix the settings so we won't show the dialog. ...
                         break;
                 }
             }
        });
        */

        // if (requestingLocationUpdates) {
            Log.v(TAG, "Have requested location updates");
            startLocationUpdates();
        // } else {
        //     Log.v(TAG, "Not starting location updates");
        // }
    }

    protected void startLocationUpdates() {
        Log.v(TAG, "Starting location updates");
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Starting location updates: have permissions");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            Log.v(TAG, "Starting location updates: do not have permissions");
            Toast.makeText(context, "Do not have permissions", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO
        Log.v(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO
        Log.v(TAG, "onConnectionFailed()");
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO
        Toast.makeText(context, location.toString(), Toast.LENGTH_SHORT).show();
        Log.v(TAG, "Location: ");
    }

}
