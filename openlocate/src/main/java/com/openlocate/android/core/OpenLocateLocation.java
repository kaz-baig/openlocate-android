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
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String ACCURACY_KEY = "accuracy";
    private static final String SPEED_KEY = "speed";
    private static final String BEARING_KEY = "bearing";
    private static final String RECORDED_AT_KEY = "recorded_at";
    private static final String ALTITUDE_KEY = "altitude";

    private double latitude;
    private double longitude;
    private float accuracy;
    private float speed;
    private float bearing;
    private long recordedAt;
    private double altitude;

    // For testing
    OpenLocateLocation(double lat, double lng) {
        latitude = lat;
        longitude = lng;
    }

    OpenLocateLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = location.getAccuracy();
        speed = location.getSpeed();
        bearing = location.getBearing();
        recordedAt = location.getTime();
        altitude = location.getAltitude();
    }

    OpenLocateLocation(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            latitude = jsonObject.getDouble(LATITUDE_KEY);
            longitude = jsonObject.getDouble(LONGITUDE_KEY);
            accuracy = Float.valueOf(jsonObject.getString(ACCURACY_KEY));
            speed = Float.valueOf(jsonObject.getString(SPEED_KEY));
            bearing = Float.valueOf(jsonObject.getString(BEARING_KEY));
            altitude = jsonObject.getDouble(ALTITUDE_KEY);
            recordedAt = jsonObject.getLong(RECORDED_AT_KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject getJson() {
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject()
                    .put(LATITUDE_KEY, latitude)
                    .put(LONGITUDE_KEY, longitude)
                    .put(ACCURACY_KEY, accuracy)
                    .put(SPEED_KEY, speed)
                    .put(BEARING_KEY, bearing)
                    .put(RECORDED_AT_KEY, recordedAt)
                    .put(ALTITUDE_KEY, altitude);
        } catch (JSONException exception) {
            jsonObject = null;
        }

        return jsonObject;
    }

}
