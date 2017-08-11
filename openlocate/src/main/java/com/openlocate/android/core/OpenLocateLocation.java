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

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

final class OpenLocateLocation implements JsonObjectType {

    class Keys {
        static final String LATITUDE = "latitude";
        static final String LONGITUDE = "longitude";
        static final String HORIZONTAL_ACCURACY = "horizontal_accuracy";
        static final String TIMESTAMP = "utc_timestamp";
        static final String COURSE = "course";
        static final String SPEED = "speed";
        static final String ALTITUDE = "altitude";

        static final String AD_ID = "ad_id";
        static final String AD_OPT_OUT = "ad_opt_out";
        static final String AD_TYPE = "id_type";

        static final String PROVIDER_SOURCE_ID = "provider_source_id";
        static final String EMAIL_ADDR = "email_addr";
        static final String EMAIL_ADDR_SHA256 = "email_addr_sha256";
        static final String USER_ID_3P = "user_id_3p";

        static final String IS_CHARGING = "is_charging";
        static final String DEVICE_MANUFACTURER = "device_manufacturer";
        static final String DEVICE_MODEL = "device_model";
        static final String OPERATING_SYSTEM = "os";

        static final String LOCATION_METHOD = "location_method";
        static final String LOCATION_CONTEXT = "location_context";

        static final String IP_ADDRESS = "ip_address";
        static final String CARRIER_NAME = "carrier_name";
        static final String CONNECTION_TYPE = "connection_type";
        static final String WIFI_SSID = "wifi_ssid";
        static final String WIFI_BSSID = "wifi_bssid";
    }

    private static final String ADVERTISING_ID_TYPE = "aaid";

    private LocationInfo location;
    private AdvertisingInfo advertisingInfo;
    private DeviceInfo deviceInfo;
    private NetworkInfo networkInfo;
    private UserInfo userInfo;
    private LocationProvider provider;

    OpenLocateLocation(
            Location location,
            AdvertisingInfo advertisingInfo,
            DeviceInfo deviceInfo,
            NetworkInfo networkInfo,
            UserInfo userInfo,
            LocationProvider provider) {
        this.location = new LocationInfo(location);
        this.advertisingInfo = advertisingInfo;
        this.deviceInfo = deviceInfo;
        this.networkInfo = networkInfo;
        this.userInfo = userInfo;
        this.provider = provider;
    }

    OpenLocateLocation(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);

            location = new LocationInfo();
            location.setLatitude(json.getDouble(Keys.LATITUDE));
            location.setLongitude(json.getDouble(Keys.LONGITUDE));
            location.setHorizontalAccuracy(Float.parseFloat(json.getString(Keys.HORIZONTAL_ACCURACY)));
            location.setTimeStamp(json.getLong(Keys.TIMESTAMP));
            location.setAltitude(json.getDouble(Keys.ALTITUDE));
            location.setCourse(Float.parseFloat(json.getString(Keys.COURSE)));
            location.setSpeed(Float.parseFloat(json.getString(Keys.SPEED)));

            advertisingInfo = new AdvertisingInfo(
                    json.getString(Keys.AD_ID),
                    json.getBoolean(Keys.AD_OPT_OUT)
            );

            deviceInfo = new DeviceInfo(
                    json.getString(Keys.DEVICE_MANUFACTURER),
                    json.getString(Keys.DEVICE_MODEL),
                    json.getString(Keys.OPERATING_SYSTEM),
                    json.getBoolean(Keys.IS_CHARGING)
            );

            networkInfo = new NetworkInfo(
                    json.getString(Keys.CARRIER_NAME),
                    json.getString(Keys.WIFI_SSID),
                    json.getString(Keys.WIFI_BSSID),
                    json.getString(Keys.CONNECTION_TYPE)
            );

            userInfo = new UserInfo(
                    json.getString(Keys.EMAIL_ADDR),
                    json.getString(Keys.USER_ID_3P),
                    json.getString(Keys.PROVIDER_SOURCE_ID)
            );

            provider = LocationProvider.valueOf(json.getString(Keys.LOCATION_METHOD));
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject
                    .put(Keys.LATITUDE, location.getLatitude())
                    .put(Keys.LONGITUDE, location.getLongitude())
                    .put(Keys.HORIZONTAL_ACCURACY, String.valueOf(location.getHorizontalAccuracy()))
                    .put(Keys.TIMESTAMP, location.getTimeStamp())
                    .put(Keys.COURSE, String.valueOf(location.getCourse()))
                    .put(Keys.SPEED, String.valueOf(location.getSpeed()))
                    .put(Keys.ALTITUDE, location.getAltitude())

                    .put(Keys.AD_ID, advertisingInfo.getAdvertisingId())
                    .put(Keys.AD_OPT_OUT, advertisingInfo.isLimitedAdTrackingEnabled())
                    .put(Keys.AD_TYPE, ADVERTISING_ID_TYPE)

                    .put(Keys.DEVICE_MANUFACTURER, deviceInfo.getManufacturer())
                    .put(Keys.DEVICE_MODEL, deviceInfo.getModel())
                    .put(Keys.IS_CHARGING, deviceInfo.isCharging())
                    .put(Keys.OPERATING_SYSTEM, deviceInfo.getOperatingSystem())

                    .put(Keys.CARRIER_NAME, networkInfo.getCarrierName())
                    .put(Keys.WIFI_SSID, networkInfo.getWifiSsid())
                    .put(Keys.WIFI_BSSID, networkInfo.getWifiBssid())
                    .put(Keys.CONNECTION_TYPE, networkInfo.getConnectionType())

                    .put(Keys.EMAIL_ADDR, userInfo.getEmail())
                    .put(Keys.EMAIL_ADDR_SHA256, userInfo.getEmailSha256())
                    .put(Keys.USER_ID_3P, userInfo.getUserId3p())
                    .put(Keys.PROVIDER_SOURCE_ID, userInfo.getProviderSourceId())

                    .put(Keys.LOCATION_METHOD, provider.toString());
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private class LocationInfo {

        private double latitude;
        private double longitude;
        private float horizontalAccuracy;
        private long timeStamp;
        private float speed;
        private float course;
        private double altitude;

        private LocationInfo() {

        }

        private LocationInfo(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            horizontalAccuracy = location.getAccuracy();
            timeStamp = location.getTime();
            speed = location.getSpeed();
            course = location.getBearing();
            altitude = location.getAltitude();
        }

        double getLatitude() {
            return latitude;
        }

        void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        double getLongitude() {
            return longitude;
        }

        void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        float getHorizontalAccuracy() {
            return horizontalAccuracy;
        }

        void setHorizontalAccuracy(float horizontalAccuracy) {
            this.horizontalAccuracy = horizontalAccuracy;
        }

        long getTimeStamp() {
            return timeStamp;
        }

        void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        float getSpeed() {
            return speed;
        }

        void setSpeed(float speed) {
            this.speed = speed;
        }

        float getCourse() {
            return course;
        }

        void setCourse(float course) {
            this.course = course;
        }

        double getAltitude() {
            return altitude;
        }

        void setAltitude(double altitude) {
            this.altitude = altitude;
        }
    }
}
