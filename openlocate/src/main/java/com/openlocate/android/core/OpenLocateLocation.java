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

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public final class OpenLocateLocation implements JsonObjectType {

    class Keys {
        static final String LATITUDE = "latitude";
        static final String LONGITUDE = "longitude";
        static final String HORIZONTAL_ACCURACY = "horizontal_accuracy";
        static final String TIMESTAMP = "utc_timestamp";
        static final String AD_ID = "ad_id";
        static final String AD_OPT_OUT = "ad_opt_out";
        static final String AD_TYPE = "id_type";

        static final String COURSE = "course";
        static final String SPEED = "speed";
        static final String ALTITUDE = "altitude";

        static final String IS_CHARGING = "is_charging";
        static final String DEVICE_MANUFACTURER = "device_manufacturer";
        static final String DEVICE_MODEL = "device_model";
        static final String OPERATING_SYSTEM = "os";

        static final String LOCATION_METHOD = "location_method";
        static final String LOCATION_CONTEXT = "location_context";

        static final String CARRIER_NAME = "carrier_name";
        static final String CONNECTION_TYPE = "connection_type";
        static final String WIFI_SSID = "wifi_ssid";
        static final String WIFI_BSSID = "wifi_bssid";
    }

    private static final String ADVERTISING_ID_TYPE = "aaid";

    private LocationInfo location;
    private AdvertisingIdClient.Info advertisingInfo;
    private DeviceInfo deviceInfo;
    private NetworkInfo networkInfo;
    private LocationProvider provider;
    private LocationContext locationContext;

    public LocationInfo getLocation() {
        return location;
    }

    public void setLocation(LocationInfo location) {
        this.location = location;
    }

    public AdvertisingIdClient.Info getAdvertisingInfo() {
        return advertisingInfo;
    }

    public void setAdvertisingInfo(AdvertisingIdClient.Info advertisingInfo) {
        this.advertisingInfo = advertisingInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
    }

    public LocationProvider getProvider() {
        return provider;
    }

    public void setProvider(LocationProvider provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "OpenLocateLocation{" +
                "location=" + location +
                ", advertisingInfo=" + advertisingInfo +
                ", deviceInfo=" + deviceInfo +
                ", networkInfo=" + networkInfo +
                ", provider=" + provider +
                ", locationContext=" + locationContext +
                '}';
    }

    public static OpenLocateLocation from(Location location,
                                          AdvertisingIdClient.Info advertisingInfo,
                                          DeviceInfo deviceInfo,
                                          NetworkInfo networkInfo,
                                          LocationProvider provider, LocationContext locationContext) {
        return new OpenLocateLocation(location, advertisingInfo, deviceInfo, networkInfo, provider, locationContext);

    }

    private OpenLocateLocation(
            Location location,
            AdvertisingIdClient.Info advertisingInfo,
            DeviceInfo deviceInfo,
            NetworkInfo networkInfo,
            LocationProvider provider, LocationContext locationContext) {
        this.location = new LocationInfo(location);
        this.advertisingInfo = advertisingInfo;
        this.deviceInfo = deviceInfo;
        this.networkInfo = networkInfo;
        this.provider = provider;
        this.locationContext = locationContext;
    }

    OpenLocateLocation(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);

            location = new LocationInfo();
            location.setLatitude(json.getDouble(Keys.LATITUDE));
            location.setLongitude(json.getDouble(Keys.LONGITUDE));
            location.setHorizontalAccuracy(Float.parseFloat(json.getString(Keys.HORIZONTAL_ACCURACY)));
            location.setTimeStampSecs(json.getLong(Keys.TIMESTAMP));
            location.setAltitude(json.getDouble(Keys.ALTITUDE));
            location.setCourse(Float.parseFloat(json.getString(Keys.COURSE)));
            location.setSpeed(Float.parseFloat(json.getString(Keys.SPEED)));

            advertisingInfo = new AdvertisingIdClient.Info(
                    json.getString(Keys.AD_ID),
                    json.getBoolean(Keys.AD_OPT_OUT)
            );

            deviceInfo = DeviceInfo.from(
                    json.getString(Keys.DEVICE_MANUFACTURER),
                    json.getString(Keys.DEVICE_MODEL),
                    json.getString(Keys.OPERATING_SYSTEM),
                    json.getBoolean(Keys.IS_CHARGING)
            );

            networkInfo = NetworkInfo.from(
                    json.getString(Keys.CARRIER_NAME),
                    json.getString(Keys.WIFI_SSID),
                    json.getString(Keys.WIFI_BSSID),
                    json.getString(Keys.CONNECTION_TYPE)
            );

            provider = LocationProvider.get(json.getString(Keys.LOCATION_METHOD));

            locationContext = LocationContext.get(json.getString(Keys.LOCATION_CONTEXT));


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
                    .put(Keys.TIMESTAMP, location.getTimeStampSecs())
                    .put(Keys.COURSE, String.valueOf(location.getCourse()))
                    .put(Keys.SPEED, String.valueOf(location.getSpeed()))
                    .put(Keys.ALTITUDE, location.getAltitude())

                    .put(Keys.AD_ID, advertisingInfo.getId())
                    .put(Keys.AD_OPT_OUT, advertisingInfo.isLimitAdTrackingEnabled())
                    .put(Keys.AD_TYPE, ADVERTISING_ID_TYPE)

                    .put(Keys.DEVICE_MANUFACTURER, deviceInfo.getManufacturer())
                    .put(Keys.DEVICE_MODEL, deviceInfo.getModel())
                    .put(Keys.IS_CHARGING, deviceInfo.isCharging())
                    .put(Keys.OPERATING_SYSTEM, deviceInfo.getOperatingSystem())

                    .put(Keys.CARRIER_NAME, networkInfo.getCarrierName())
                    .put(Keys.WIFI_SSID, networkInfo.getWifiSsid())
                    .put(Keys.WIFI_BSSID, networkInfo.getWifiBssid())
                    .put(Keys.CONNECTION_TYPE, networkInfo.getConnectionType())

                    .put(Keys.LOCATION_METHOD, provider.getValue())
                    .put(Keys.LOCATION_CONTEXT, locationContext.getValue());
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public class LocationInfo {

        private double latitude;
        private double longitude;
        private float horizontalAccuracy;
        private long timeStampSecs;
        private float speed;
        private float course;
        private double altitude;

        LocationInfo() {

        }

        LocationInfo(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            horizontalAccuracy = location.getAccuracy();
            timeStampSecs = TimeUnit.MILLISECONDS.toSeconds(location.getTime());
            speed = location.getSpeed();
            course = location.getBearing();
            altitude = location.getAltitude();
        }

        public double getLatitude() {
            return latitude;
        }

        void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getHorizontalAccuracy() {
            return horizontalAccuracy;
        }

        void setHorizontalAccuracy(float horizontalAccuracy) {
            this.horizontalAccuracy = horizontalAccuracy;
        }

        long getTimeStampSecs() {
            return timeStampSecs;
        }

        void setTimeStampSecs(long timeStampSecs) {
            this.timeStampSecs = timeStampSecs;
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

        @Override
        public String toString() {
            return "LocationInfo{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", horizontalAccuracy=" + horizontalAccuracy +
                    ", timeStampSecs=" + timeStampSecs +
                    ", speed=" + speed +
                    ", course=" + course +
                    ", altitude=" + altitude +
                    '}';
        }
    }

}
