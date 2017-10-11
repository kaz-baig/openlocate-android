package com.openlocate.android.core;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.openlocate.android.config.Configuration;

public class InformationFields {

    private final Context context;
    private Configuration configuration;

    private String manufacturer;
    private String model;
    private String operatingSystem;
    private boolean isCharging;

    private String carrierName;
    private String wifiSsid;
    private String wifiBssid;
    private String connectionType;
    private LocationProvider locationProvider;
    private LocationContext locationContext;

    private static final String BASE_NAME = "Android";

    public static InformationFields from(Context context, Configuration configuration) {
        return new InformationFields(context, configuration);
    }

    private InformationFields(Context context, Configuration configuration) {

        this.configuration = configuration;
        this.context = context;

        updateDeviceInfo();

        if (!this.configuration.isCarrierNameCollectionDisabled()) {
            updateCarrierName();
        }

        if (!this.configuration.isWifiCollectionDisabled()) {
            updateWifiInfo();
        }

        if (!this.configuration.isConnectionTypeCollectionDisabled()) {
            updateConnectionType();
        }

        if (!this.configuration.isLocationMethodCollectionDisabled()) {
            updateLocationProvider();
        }
        if (!this.configuration.isLocationContextCollectionDisabled()) {
            updateLocationContext();
        }
    }

    private void updateDeviceInfo() {

        if (!configuration.isDeviceManufacturerCollectionDisabled()) {
            this.model = Build.MODEL;
        }

        if (!configuration.isOperaringSystemCollectionDisbaled()) {
            this.operatingSystem = BASE_NAME + " " + Build.VERSION.RELEASE;
        }

        if (!configuration.isChargingInfoCollectionDisabled()) {
            Intent batteryIntent = context.registerReceiver(
                    null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            );
            this.isCharging = isDeviceCharging(batteryIntent);
        }
    }

    private void updateLocationContext() {
        locationContext = LocationContext.getLocationContext();
    }

    private void updateLocationProvider() {
        locationProvider = LocationProvider.getLocationProvider(context);
    }

    private boolean isDeviceCharging(Intent batteryIntent) {
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
        return status == BatteryManager.BATTERY_STATUS_CHARGING;
    }

    private void updateCarrierName() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        carrierName = telephonyManager.getNetworkOperatorName();
    }

    private void updateWifiInfo() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        wifiSsid = wifiInfo.getSSID();
        wifiBssid = wifiInfo.getBSSID();
    }

    private void updateConnectionType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connected = connectivityManager.getActiveNetworkInfo().isConnected();

        if (!connected) {
            connectionType = "none";
            return;
        }

        int type = connectivityManager.getActiveNetworkInfo().getType();

        switch (type) {
            case ConnectivityManager.TYPE_WIFI:
                connectionType = "wifi";
                break;
            case ConnectivityManager.TYPE_MOBILE:
                connectionType = "cellular";
                break;
            default:
                connectionType = "unknown";
        }
    }


    public Context getContext() {
        return context;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public String getWifiSsid() {
        return wifiSsid;
    }

    public String getWifiBssid() {
        return wifiBssid;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public LocationProvider getLocationProvider() {
        return locationProvider;
    }

    public LocationContext getLocationContext() {
        return locationContext;
    }
}
