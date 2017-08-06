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

final class DetailedLocation implements JsonObjectType, DatabaseJson {
    private static final String PROVIDER_KEY = "provider";
    private static final String SOURCE_ID_KEY = "source_id";
    private static final String LOCATION_KEY = "location";
    private static final String ADVERTISING_ID_KEY = "advertising_id";
    private static final String LIMITED_AD_TRACKING_ENABLED_KEY = "limited_ad_tracking";
    private static final String ADVERTISING_ID_TYPE_KEY = "advertising_id_type";

    private static final String ADVERTISING_ID_TYPE = "aaid";

    private OpenLocateLocation location;
    private String sourceId;
    private LocationProvider provider;
    private AdvertisingInfo advertisingInfo;

    //For testing only
    DetailedLocation(double lat, double lng, String sourceId, LocationProvider provider, AdvertisingInfo info) {
        this.location = new OpenLocateLocation(lat, lng);
        this.sourceId = sourceId;
        this.provider = provider;
        this.advertisingInfo = info;
    }

    DetailedLocation(Location location, String sourceId, LocationProvider provider, AdvertisingInfo info) {
        this.location = new OpenLocateLocation(location);
        this.sourceId = sourceId;
        this.provider = provider;
        this.advertisingInfo = info;
    }

    DetailedLocation(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);

            provider = LocationProvider.GPS;
            sourceId = json.getString(SOURCE_ID_KEY);
            location = new OpenLocateLocation(json.getString(LOCATION_KEY));
            advertisingInfo = new AdvertisingInfo(
                    json.getString(ADVERTISING_ID_KEY),
                    json.getBoolean(LIMITED_AD_TRACKING_ENABLED_KEY)
            );
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public JSONObject getJson() {
        JSONObject jsonObject = location.getJson();

        try {
            jsonObject
                    .put(SOURCE_ID_KEY, sourceId)
                    .put(PROVIDER_KEY, provider.toString())
                    .put(ADVERTISING_ID_KEY, advertisingInfo.getAdvertisingId())
                    .put(LIMITED_AD_TRACKING_ENABLED_KEY, advertisingInfo.isLimitedAdTrackingEnabled())
                    .put(ADVERTISING_ID_TYPE_KEY, ADVERTISING_ID_TYPE);
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public String getDatabaseJsonString() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(LOCATION_KEY, location.getJson());
            jsonObject.put(SOURCE_ID_KEY, sourceId);
            jsonObject.put(PROVIDER_KEY, provider.toString());
            jsonObject.put(ADVERTISING_ID_KEY, advertisingInfo.getAdvertisingId());
            jsonObject.put(LIMITED_AD_TRACKING_ENABLED_KEY, advertisingInfo.isLimitedAdTrackingEnabled());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}
