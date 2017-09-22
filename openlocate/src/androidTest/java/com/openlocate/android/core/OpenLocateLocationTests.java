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
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class OpenLocateLocationTests {

    @Test
    public void testOpenLocateConstructor() {
        // Given
        double lat = 10.40;
        double lng = 10.234;
        double accuracy = 40.43;
        boolean adOptOut = true;
        String adId = "1234";

        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setAccuracy((float) accuracy);

        AdvertisingIdClient.Info info = new AdvertisingIdClient.Info(adId, adOptOut);

        OpenLocateLocation openLocateLocation = new OpenLocateLocation(location, info);

        // When
        JSONObject json = openLocateLocation.getJson();

        // Then
        assertNotNull(openLocateLocation);
        try {
            assertEquals(json.getDouble(OpenLocateLocation.Keys.LATITUDE), lat, 0.0d);
            assertEquals(json.getDouble(OpenLocateLocation.Keys.LONGITUDE), lng, 0.0d);
            assertEquals(json.getDouble(OpenLocateLocation.Keys.HORIZONTAL_ACCURACY), accuracy, 0.1);
            assertEquals(json.getBoolean(OpenLocateLocation.Keys.AD_OPT_OUT), adOptOut);
            assertEquals(json.getString(OpenLocateLocation.Keys.AD_ID), adId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
