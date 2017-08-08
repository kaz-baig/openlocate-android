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

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class OpenLocateLocationTests {
    private double lat = 10.40;
    private double lng = 10.234;
    private String accuracy = "40.43";
    private boolean adOptOut = true;
    private String adId = "1234";
    private long timestamp = 341;
    private String providerSourceId = "2345";

    private JSONObject getJson() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(OpenLocateLocation.Keys.LATITUDE, lat);
            jsonObject.put(OpenLocateLocation.Keys.LONGITUDE, lng);
            jsonObject.put(OpenLocateLocation.Keys.AD_OPT_OUT, adOptOut);
            jsonObject.put(OpenLocateLocation.Keys.AD_ID, adId);
            jsonObject.put(OpenLocateLocation.Keys.HORIZONTAL_ACCURACY, accuracy);
            jsonObject.put(OpenLocateLocation.Keys.TIMESTAMP, timestamp);
            jsonObject.put(OpenLocateLocation.Keys.PROVIDER_SOURCE_ID, providerSourceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Test
    public void testOpenTableJsonConstructor() {
        // Given
        JSONObject jsonObject = getJson();

        // When
        OpenLocateLocation location = new OpenLocateLocation(jsonObject.toString());
        JSONObject json = location.getJson();

        // Then
        assertNotNull(location);
        try {
            assertEquals(json.getDouble(OpenLocateLocation.Keys.LATITUDE), lat, 0.0d);
            assertEquals(json.getDouble(OpenLocateLocation.Keys.LONGITUDE), lng, 0.0d);
            assertEquals(json.getString(OpenLocateLocation.Keys.HORIZONTAL_ACCURACY), accuracy);
            assertEquals(json.getLong(OpenLocateLocation.Keys.TIMESTAMP), timestamp);
            assertEquals(json.getString(OpenLocateLocation.Keys.PROVIDER_SOURCE_ID), providerSourceId);
            assertEquals(json.getBoolean(OpenLocateLocation.Keys.AD_OPT_OUT), adOptOut);
            assertEquals(json.getString(OpenLocateLocation.Keys.AD_ID), adId);
            assertEquals(json.getLong(OpenLocateLocation.Keys.TIMESTAMP), timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
