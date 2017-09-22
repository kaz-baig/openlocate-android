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
    }

    private static final String ADVERTISING_ID_TYPE = "aaid";

    private TempLocation location;
    private AdvertisingIdClient.Info advertisingInfo;

    OpenLocateLocation(Location location, AdvertisingIdClient.Info info) {
        this.location = new TempLocation(location);
        this.advertisingInfo = info;
    }

    OpenLocateLocation(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);

            location = new TempLocation();
            location.setLatitude(json.getDouble(Keys.LATITUDE));
            location.setLongitude(json.getDouble(Keys.LONGITUDE));
            location.setHorizontalAccuracy(json.getDouble(Keys.HORIZONTAL_ACCURACY));
            location.setTimeStamp(json.getLong(Keys.TIMESTAMP));

            advertisingInfo = new AdvertisingIdClient.Info(
                    json.getString(Keys.AD_ID),
                    json.getBoolean(Keys.AD_OPT_OUT)
            );
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
                    .put(Keys.HORIZONTAL_ACCURACY, location.getHorizontalAccuracy())
                    .put(Keys.TIMESTAMP, location.getTimeStamp())
                    .put(Keys.AD_ID, advertisingInfo.getId())
                    .put(Keys.AD_OPT_OUT, advertisingInfo.isLimitAdTrackingEnabled())
                    .put(Keys.AD_TYPE, ADVERTISING_ID_TYPE);
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private class TempLocation {

        private double latitude;
        private double longitude;
        private double horizontalAccuracy;
        private long timeStamp;

        TempLocation() {

        }

        TempLocation(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            horizontalAccuracy = location.getAccuracy();
            timeStamp = TimeUnit.MILLISECONDS.toSeconds(location.getTime());
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

        double getHorizontalAccuracy() {
            return horizontalAccuracy;
        }

        void setHorizontalAccuracy(double horizontalAccuracy) {
            this.horizontalAccuracy = horizontalAccuracy;
        }

        long getTimeStamp() {
            return timeStamp;
        }

        void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }

    public double getLatitude() {
        return this.location.getLatitude();
    }

    public double getLongitude() {
        return this.location.getLongitude();
    }

    public double getHorizontalAccuracy() {
        return this.location.getHorizontalAccuracy();
    }

    public long getTimeStamp() {
        return this.location.getTimeStamp();
    }

    public String getAdvertisingId() {
        return this.advertisingInfo.getId();
    }

    public boolean isLimitedAdTrackingEnabled() {
        return this.advertisingInfo.isLimitAdTrackingEnabled();
    }
}
