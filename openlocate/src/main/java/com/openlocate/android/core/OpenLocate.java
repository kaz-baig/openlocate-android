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

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.openlocate.android.R;
import com.openlocate.android.config.Configuration;
import com.openlocate.android.exceptions.IllegalConfigurationException;
import com.openlocate.android.exceptions.InvalidSourceException;
import com.openlocate.android.exceptions.LocationConfigurationException;
import com.openlocate.android.exceptions.LocationPermissionException;
import com.openlocate.android.exceptions.LocationServiceConflictException;

public class OpenLocate implements OpenLocateLocationTracker {
    private static OpenLocate sharedInstance = null;

    private static String TAG = OpenLocate.class.getSimpleName();

    private String providerSourceId;
    private String providerId;

    private Context context;
    private Configuration configuration;
    private Logger logger;

    private OpenLocate(Context context) {
        this.context = context;

        SharedPreferencesManager manager = new SharedPreferencesManager(context);

        String baseUrl = manager.getBaseUrl();
        String tcpHost = manager.getTcpHost();
        int tcpPort = manager.getTcpPort();

        this.configuration = new Configuration.Builder()
                .setBaseUrl(baseUrl)
                .setTcpHost(tcpHost)
                .setTcpPort(tcpPort)
                .build();

        if (configuration.isTcpConfigured()) {
            setupRemoteLogger();
        } else {
            setupConsoleLogger();
        }
    }

    public static OpenLocate getInstance(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new OpenLocate(context);
        }

        return sharedInstance;
    }

    @Override
    public void startTracking() throws InvalidSourceException, LocationServiceConflictException, IllegalConfigurationException {
        boolean startTracking = hasTrackingCapabilities();

        if (!startTracking) {
            // TODO: do something here
            return;
        }

        FetchAdvertisingInfoTask task = new FetchAdvertisingInfoTask(context, new FetchAdvertisingInfoTaskCallback() {
            @Override
            public void onAdvertisingInfoTaskExecute(AdvertisingIdClient.Info info) {
                onFetchAdvertisingInfo(info);
            }
        });
        task.execute();
    }

    private void onFetchAdvertisingInfo(AdvertisingIdClient.Info info) {
        Intent intent = new Intent(context, LocationService.class);

        updateSourceId(intent);
        updateConfiguration(intent);

        if (info != null) {
            updateAdvertisingInfo(intent, info.getId(), info.isLimitAdTrackingEnabled());
        }

        context.startService(intent);
    }

    private void updateConfiguration(Intent intent) {
        intent.putExtra(Constants.BASE_URL_KEY, configuration.getBaseUrl());
        intent.putExtra(Constants.HOST_KEY, configuration.getTcpHost());
        intent.putExtra(Constants.PORT_KEY, configuration.getTcpPort());
    }

    private void updateSourceId(Intent intent) {
        intent.putExtra(Constants.PROVIDER_SOURCE_ID_KEY, providerSourceId);
        intent.putExtra(Constants.PROVIDER_KEY, providerId);
    }

    private void updateAdvertisingInfo(Intent intent, String advertisingId, boolean isLimitedAdTrackingEnabled) {
        intent.putExtra(Constants.ADVERTISING_ID_KEY, advertisingId);
        intent.putExtra(Constants.LIMITED_AD_TRACKING_ENABLED_KEY, isLimitedAdTrackingEnabled);
    }

    private boolean hasTrackingCapabilities() throws InvalidSourceException, LocationServiceConflictException, IllegalConfigurationException {
        validateProviderSourceId();
        validateProviderId();
        validateLocationService();
        validateConfiguration();
        validateLocationPermission();
        validateLocationEnabled();
        return true;
    }

    private void validateProviderSourceId() throws InvalidSourceException {
        if (providerSourceId == null || providerSourceId.isEmpty()) {
            logger.e(context.getString(R.string.invalid_source_message));
            throw new InvalidSourceException(
                    context.getString(R.string.invalid_source_message)
            );
        }
    }

    private void validateProviderId() throws InvalidSourceException {
        if (providerId == null || providerId.isEmpty()) {
            logger.e(context.getString(R.string.invalid_source_message));
            throw new InvalidSourceException(
                    context.getString(R.string.invalid_source_message)
            );
        }
    }

    private void validateLocationService() throws LocationServiceConflictException {
        if (ServiceUtils.isServiceRunning(LocationService.class, context)) {
            logger.e(context.getString(R.string.location_service_running_message));
            throw new LocationServiceConflictException(
                    context.getString(R.string.location_service_running_message)
            );
        }
    }

    private void validateConfiguration() throws IllegalConfigurationException {
        if (!configuration.isValid()) {
            logger.e(context.getString(R.string.illegal_configuration_message));
            throw new IllegalConfigurationException(
                    context.getString(R.string.illegal_configuration_message)
            );
        }
    }

    private void validateLocationPermission() throws LocationPermissionException {
        if (!LocationService.hasLocationPermission(context)) {
            logger.e(context.getString(R.string.location_permission_denied_message));
            throw new LocationPermissionException(
                    context.getString(R.string.location_permission_denied_message)
            );
        }
    }

    private void validateLocationEnabled() throws LocationConfigurationException {
        if (!LocationService.isLocationEnabled(context)) {
            logger.e(context.getString(R.string.location_disabled_message));
            throw new LocationConfigurationException(
                    context.getString(R.string.location_disabled_message)
            );
        }
    }

    @Override
    public void stopTracking() {
        Intent intent = new Intent(context, LocationService.class);
        context.stopService(intent);
    }

    @Override
    public String getProviderSourceId() {
        return providerSourceId;
    }

    @Override
    public void setProviderSourceId(String providerSourceId) {
        this.providerSourceId = providerSourceId;
    }

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    @Override
    public void configure(Configuration configuration) throws IllegalConfigurationException {
        if (!configuration.isValid()) {
            logger.e(context.getString(R.string.illegal_configuration_message));
            throw new IllegalConfigurationException(
                    context.getString(R.string.illegal_configuration_message)
            );
        }

        this.configuration = configuration;

        SharedPreferencesManager manager = new SharedPreferencesManager(context);
        manager.setBaseUrl(configuration.getBaseUrl());
        manager.setTcpHost(configuration.getTcpHost());
        manager.setTcpPort(configuration.getTcpPort());

        if (configuration.isTcpConfigured()) {
            setupRemoteLogger();
        }
    }

    @Override
    public boolean isTracking() {
        return ServiceUtils.isServiceRunning(LocationService.class, context);
    }

    private void setupRemoteLogger() {
        TcpClient tcpClient = new TcpClientImpl(configuration.getTcpHost(), configuration.getTcpPort());
        logger = new RemoteLogger(tcpClient, LogLevel.INFO);
    }

    private void setupConsoleLogger() {
        logger = new ConsoleLogger(LogLevel.INFO);
    }
}
