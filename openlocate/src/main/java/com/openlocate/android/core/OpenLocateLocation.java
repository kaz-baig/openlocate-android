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
        static final String AD_ID = "ad_id";
        static final String AD_OPT_OUT = "ad_opt_out";
        static final String AD_TYPE = "id_type";
        static final String PROVIDER_SOURCE_ID = "provider_source_id";
    }

    private static final String ADVERTISING_ID_TYPE = "aaid";
    private static final String PROVIDER = "OpenLocate";

    private TempLocation location;
    private String providerSourceId;
    private AdvertisingInfo advertisingInfo;

    OpenLocateLocation(Location location, String providerSourceId, AdvertisingInfo info) {
        this.location = new TempLocation(location);
        this.providerSourceId = providerSourceId;
        this.advertisingInfo = info;
    }

    OpenLocateLocation(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);

            location = new TempLocation();
            location.setLatitude(json.getDouble(Keys.LATITUDE));
            location.setLongitude(json.getDouble(Keys.LONGITUDE));
            location.setHorizontalAccuracy(Float.parseFloat(json.getString(Keys.HORIZONTAL_ACCURACY)));
            location.setTimeStamp(json.getLong(Keys.TIMESTAMP));

            advertisingInfo = new AdvertisingInfo(
                    json.getString(Keys.AD_ID),
                    json.getBoolean(Keys.AD_OPT_OUT)
            );

            providerSourceId = json.getString(Keys.PROVIDER_SOURCE_ID);
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
                    .put(Keys.AD_ID, advertisingInfo.getAdvertisingId())
                    .put(Keys.AD_OPT_OUT, advertisingInfo.isLimitedAdTrackingEnabled())
                    .put(Keys.AD_TYPE, ADVERTISING_ID_TYPE)
                    .put(Keys.PROVIDER_SOURCE_ID, providerSourceId);
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private class TempLocation {

        private double latitude;
        private double longitude;
        private float horizontalAccuracy;
        private long timeStamp;

        TempLocation() {

        }

        TempLocation(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            horizontalAccuracy = location.getAccuracy();
            timeStamp = location.getTime();
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
    }
}
