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
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

final class NetworkInfo {
    private String carrierName;
    private String wifiSsid;
    private String wifiBssid;
    private String connectionType;

    private NetworkInfo(String carrierName, String wifiSsid, String wifiBssid, String connectionType) {
        this.carrierName = carrierName;
        this.wifiSsid = wifiSsid;
        this.wifiBssid = wifiBssid;
        this.connectionType = connectionType;
    }

    private NetworkInfo(Context context) {
        updateCarrierName(context);
        updateWifiInfo(context);
        updateConnectionType(context);
    }

    public static NetworkInfo from(Context context) {
        return new NetworkInfo(context);
    }

    public static NetworkInfo from(String carrierName, String wifiSsid, String wifiBssid, String connectionType) {
        return new NetworkInfo(carrierName, wifiSsid, wifiBssid, connectionType);
    }

    private void updateCarrierName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        carrierName = telephonyManager.getNetworkOperatorName();
    }

    private void updateWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        wifiSsid = wifiInfo.getSSID();
        wifiBssid = wifiInfo.getBSSID();
    }

    private void updateConnectionType(Context context) {
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

    String getCarrierName() {
        return carrierName;
    }

    String getWifiSsid() {
        return wifiSsid;
    }

    String getWifiBssid() {
        return wifiBssid;
    }

    String getConnectionType() {
        return connectionType;
    }

    @Override
    public String toString() {
        return "NetworkInfo{" +
                "carrierName='" + carrierName + '\'' +
                ", wifiSsid='" + wifiSsid + '\'' +
                ", wifiBssid='" + wifiBssid + '\'' +
                ", connectionType='" + connectionType + '\'' +
                '}';
    }
}
