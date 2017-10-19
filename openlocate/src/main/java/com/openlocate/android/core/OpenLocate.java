/*
 * Copyright (c) 2017 OpenLocate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.openlocate.android.core;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.openlocate.android.callbacks.OpenLocateLocationCallback;
import com.openlocate.android.config.Configuration;
import com.openlocate.android.exceptions.GooglePlayServicesNotAvailable;
import com.openlocate.android.exceptions.InvalidConfigurationException;
import com.openlocate.android.exceptions.LocationDisabledException;
import com.openlocate.android.exceptions.LocationPermissionException;

public class OpenLocate implements OpenLocateLocationTracker {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static OpenLocate sharedInstance = null;
    private static final String TAG = OpenLocate.class.getSimpleName();

    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private long locationInterval = Constants.DEFAULT_LOCATION_INTERVAL_SEC;
    private long transmissionInterval = Constants.DEFAULT_TRANSMISSION_INTERVAL_SEC;
    private LocationAccuracy accuracy = Constants.DEFAULT_LOCATION_ACCURACY;
    private Configuration configuration;

    private OpenLocate(Context context) {
        this.context = context;
    }

    public static OpenLocate getInstance(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new OpenLocate(context);
        }

        return sharedInstance;
    }

    @Override
    public void startTracking(final Configuration configuration)
            throws InvalidConfigurationException, LocationDisabledException, LocationPermissionException, GooglePlayServicesNotAvailable {
        validateTrackingCapabilities(configuration);

        FetchAdvertisingInfoTask task = new FetchAdvertisingInfoTask(context, new FetchAdvertisingInfoTaskCallback() {
            @Override
            public void onAdvertisingInfoTaskExecute(AdvertisingIdClient.Info info) {
                onFetchAdvertisingInfo(info, configuration);
            }
        });
        task.execute();
    }

    @Override
    public void getCurrentLocation(final OpenLocateLocationCallback callback) throws LocationDisabledException, LocationPermissionException {
        validateLocationPermission();
        validateLocationEnabled();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {
                            if (location == null) {
                                callback.onError(new Error("Location cannot be fetched right now."));
                            }

                            onFetchCurrentLocation(location, callback);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onError(new Error(e.getMessage()));
                        }
                    });
        } catch (SecurityException e) {
            throw new LocationPermissionException(
                    "Location permission is denied. Please enable location permission."
            );
        }
    }

    private void onFetchCurrentLocation(final Location location, final OpenLocateLocationCallback callback) {
        FetchAdvertisingInfoTask task = new FetchAdvertisingInfoTask(context, new FetchAdvertisingInfoTaskCallback() {
            @Override
            public void onAdvertisingInfoTaskExecute(AdvertisingIdClient.Info info) {

                callback.onLocationFetch(
                        OpenLocateLocation.from(
                                location,
                                info,
                                InformationFieldsFactory.collectInformationFields(context, configuration)
                        )
                );
            }
        });
        task.execute();
    }

    private void onFetchAdvertisingInfo(AdvertisingIdClient.Info info, Configuration configuration) {
        Intent intent = new Intent(context, LocationService.class);

        intent.putExtra(Constants.URL_KEY, configuration.getUrl());
        intent.putExtra(Constants.HEADER_KEY, configuration.getHeaders());
        updateLocationConfigurationInfo(intent);
        updateFieldsConfigurationInfo(intent, configuration);

        if (info != null) {
            updateAdvertisingInfo(intent, info.getId(), info.isLimitAdTrackingEnabled());
        }

        context.startService(intent);
    }

    private void updateFieldsConfigurationInfo(Intent intent, Configuration configuration) {
       intent.putExtra(Constants.INTENT_CONFIGURATION,configuration);
    }

    private void updateAdvertisingInfo(Intent intent, String advertisingId, boolean isLimitedAdTrackingEnabled) {
        intent.putExtra(Constants.ADVERTISING_ID_KEY, advertisingId);
        intent.putExtra(Constants.LIMITED_AD_TRACKING_ENABLED_KEY, isLimitedAdTrackingEnabled);
    }

    private void updateLocationConfigurationInfo(Intent intent) {
        intent.putExtra(Constants.LOCATION_ACCURACY_KEY, accuracy);
        intent.putExtra(Constants.LOCATION_INTERVAL_KEY, locationInterval);
        intent.putExtra(Constants.TRANSMISSION_INTERVAL_KEY, transmissionInterval);
    }

    private void validateTrackingCapabilities(Configuration configuration)
            throws InvalidConfigurationException, LocationPermissionException, LocationDisabledException, GooglePlayServicesNotAvailable {

        validateGooglePlayServices();
        warnIfLocationServicesAreAlreadyRunning();
        validateConfiguration(configuration);
        validateLocationPermission();
        validateLocationEnabled();
    }

    private void validateConfiguration(Configuration configuration) throws InvalidConfigurationException {
        if (!configuration.isValid()) {
            String message = "Invalid configuration. Please configure a valid url or header.";

            Log.e(TAG, message);
            throw new InvalidConfigurationException(
                    message
            );
        }
    }

    private void warnIfLocationServicesAreAlreadyRunning() {
        if (ServiceUtils.isServiceRunning(LocationService.class, context)) {
            String message = "Location tracking is already active. Please stop the previous tracking before starting,";
            Log.w(TAG, message);
        }
    }

    private void validateLocationPermission() throws LocationPermissionException {
        if (!LocationService.hasLocationPermission(context)) {
            String message = "Location permission is denied. Please enable location permission.";

            Log.e(TAG, message);
            throw new LocationPermissionException(
                    message
            );
        }
    }

    private void validateLocationEnabled() throws LocationDisabledException {
        if (!LocationService.isLocationEnabled(context)) {
            String message = "Location is switched off in the settings. Please enable it before continuing.";

            Log.e(TAG, message);
            throw new LocationDisabledException(
                    message
            );
        }
    }

    private void validateGooglePlayServices() throws GooglePlayServicesNotAvailable {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            String message = "Google Play Services is not available on this device.";
            boolean isUserResolvableError = false;
            if (apiAvailability.isUserResolvableError(resultCode)) {
                message = "Google Play Services is not enabled/installed on this device.";
                isUserResolvableError = true;
            }
            throw new GooglePlayServicesNotAvailable(message, isUserResolvableError);
        }
    }

    @Override
    public void stopTracking() {
        Intent intent = new Intent(context, LocationService.class);
        context.stopService(intent);
    }

    @Override
    public boolean isTracking() {
        return ServiceUtils.isServiceRunning(LocationService.class, context);
    }

    public long getLocationInterval() {
        return locationInterval;
    }

    public void setLocationInterval(long locationInterval) {
        this.locationInterval = locationInterval;
        broadcastLocationIntervalChanged();
    }

    public long getTransmissionInterval() {
        return transmissionInterval;
    }

    public void setTransmissionInterval(long transmissionInterval) {
        this.transmissionInterval = transmissionInterval;
        broadcastTransmissionIntervalChanged();
    }

    public LocationAccuracy getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(LocationAccuracy accuracy) {
        this.accuracy = accuracy;
        broadcastLocationAccuracyChanged();
    }

    private void broadcastLocationIntervalChanged() {
        Intent intent = new Intent(Constants.LOCATION_INTERVAL_CHANGED);
        intent.putExtra(Constants.LOCATION_INTERVAL_KEY, locationInterval);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void broadcastTransmissionIntervalChanged() {
        Intent intent = new Intent(Constants.TRANSMISSION_INTERVAL_CHANGED);
        intent.putExtra(Constants.TRANSMISSION_INTERVAL_KEY, transmissionInterval);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void broadcastLocationAccuracyChanged() {
        Intent intent = new Intent(Constants.LOCATION_ACCURACY_CHANGED);
        intent.putExtra(Constants.LOCATION_ACCURACY_KEY, accuracy);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
