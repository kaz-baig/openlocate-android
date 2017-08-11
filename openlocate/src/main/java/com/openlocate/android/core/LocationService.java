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

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.openlocate.android.exceptions.IllegalConfigurationException;

public class LocationService extends Service {

    private final static int LOCATION_REQUEST_TIME_INTERVAL_IN_MSECS = 5000;
    private final static int LOCATION_DISPATCH_TIME_INTERVAL_IN_SECS = 120;
    private final static int LOG_DISPATCH_TIME_INTERVAL_IN_SECS = 120;

    private final static int FOREGROUND_SERVICE_TAG = 1001;

    private final static String TAG = LocationService.class.getSimpleName();
    private final static String LOCATION_DISPATCH_TAG = LocationService.class.getCanonicalName() + ".location_dispatch_task";
    private final static String LOG_DISPATCH_TAG = LocationService.class.getCanonicalName() + ".log_dispatch_task";

    private GoogleApiClient googleApiClient;
    private GcmNetworkManager networkManager;

    private LocationDataSource locations;
    private LocationListener locationListener;
    private Logger logger;

    private String providerId;
    private AdvertisingInfo advertisingInfo;
    private UserInfo userInfo;

    private String baseUrl;
    private String tcpHost;
    private int tcpPort;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SQLiteOpenHelper helper = new DatabaseHelper(this);
        locations = new LocationDatabase(helper);

        logger = new DatabaseLogger(new LoggerDatabase(helper), LogLevel.INFO);
        networkManager = GcmNetworkManager.getInstance(this);
    }

    @Override
    public void onDestroy() {
        unschedulePeriodicTasks();
        stopLocationUpdates();
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setValues(intent);
        connectGoogleClient();

        /* Starting the service as foreground service. If the service is not foreground,
        service will be killed when the app is killed.
         */
        startForeground();

        return START_STICKY;
    }

    private void setValues(Intent intent) {
        providerId = intent.getStringExtra(Constants.PROVIDER_KEY);

        baseUrl = intent.getStringExtra(Constants.BASE_URL_KEY);
        tcpHost = intent.getStringExtra(Constants.HOST_KEY);
        tcpPort = intent.getIntExtra(Constants.PORT_KEY, Constants.DEFAULT_PORT);

        advertisingInfo = new AdvertisingInfo(
                intent.getStringExtra(Constants.ADVERTISING_ID_KEY),
                intent.getBooleanExtra(Constants.LIMITED_AD_TRACKING_ENABLED_KEY, false)
        );

        userInfo = new UserInfo(
                intent.getStringExtra(Constants.EMAIL_ADDR),
                intent.getStringExtra(Constants.USER_ID_3P),
                intent.getStringExtra(Constants.PROVIDER_SOURCE_ID_KEY)
        );
    }

    private void connectGoogleClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new ConnectionCallbacks())
                    .addOnConnectionFailedListener(new ConnectionFailedListener())
                    .addApi(LocationServices.API)
                    .build();
        }

        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
            return;
        }

        startLocationUpdates();
    }

    private LocationRequest getLocationRequest() {
        LocationRequest request = new LocationRequest();

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(LOCATION_REQUEST_TIME_INTERVAL_IN_MSECS);
        request.setFastestInterval(LOCATION_REQUEST_TIME_INTERVAL_IN_MSECS);

        return request;
    }

    private void startLocationUpdates() {
        try {
            LocationRequest request = getLocationRequest();
            locationListener = new LocationListener();
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, locationListener);
            schedulePeriodicTasks();
        } catch (SecurityException e) {
            locationListener = null;
            Log.e(TAG, e.getMessage());
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
        locationListener = null;
    }

    private void schedulePeriodicTasks() {
        scheduleDispatchLocationService();
        scheduleDispatchLogsService();
    }

    private void scheduleDispatchLocationService() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BASE_URL_KEY, baseUrl);
        bundle.putString(Constants.PROVIDER_KEY, providerId);

        PeriodicTask task = new PeriodicTask.Builder()
                .setExtras(bundle)
                .setService(DispatchLocationService.class)
                .setPeriod(LOCATION_DISPATCH_TIME_INTERVAL_IN_SECS)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .setPersisted(true)
                .setUpdateCurrent(true)
                .setTag(LOCATION_DISPATCH_TAG)
                .build();

        networkManager.schedule(task);
    }

    private void scheduleDispatchLogsService() {
        if (tcpHost == null || tcpHost.isEmpty() || tcpPort == Constants.DEFAULT_PORT) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(Constants.HOST_KEY, tcpHost);
        bundle.putInt(Constants.PORT_KEY, tcpPort);

        PeriodicTask task = new PeriodicTask.Builder()
                .setExtras(bundle)
                .setService(DispatchLogService.class)
                .setPeriod(LOG_DISPATCH_TIME_INTERVAL_IN_SECS)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .setPersisted(true)
                .setUpdateCurrent(true)
                .setTag(LOG_DISPATCH_TAG)
                .build();

        networkManager.schedule(task);
    }

    private void unschedulePeriodicTasks() {
        networkManager.cancelAllTasks(DispatchLocationService.class);
        networkManager.cancelAllTasks(DispatchLogService.class);
    }

    // Google Api Client Connection Callback
    private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            startLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {
            logger.e("Google Api Client Connection Suspended : " + i);
        }
    }

    // Connection Failed Listener Class
    private class ConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            logger.e("Google API Client failed to connect. Result = " + connectionResult.getErrorMessage());
        }
    }

    // OpenLocateLocation Listener class
    private class LocationListener implements com.google.android.gms.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            logger.v(location.toString());
            locations.add(
                    new OpenLocateLocation(
                            location,
                            advertisingInfo,
                            new DeviceInfo(getApplicationContext()),
                            new NetworkInfo(getApplicationContext()),
                            userInfo,
                            LocationProvider.getLocationProvider(getApplicationContext())
                    )
            );
            logger.v("COUNT - " + locations.size());
        }
    }

    static boolean isLocationEnabled(Context context) throws IllegalConfigurationException {
        try {
            int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } catch (Settings.SettingNotFoundException exception) {
            throw new IllegalConfigurationException(exception);
        }
    }

    static boolean hasLocationPermission(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void startForeground() {
        Notification notification = new Notification.Builder(this)
                .build();
        startForeground(FOREGROUND_SERVICE_TAG, notification);
    }
}
