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
package com.openlocate.example.stores;

import com.openlocate.android.core.OpenLocateLocation;
import com.openlocate.example.callbacks.SafeGraphPlaceCallback;
import com.openlocate.example.models.SafeGraphPlace;
import com.openlocate.example.network.SafeGraphPlaceClient;
import com.openlocate.example.models.SafeGraphPlaceBody;
import com.openlocate.example.network.ClientGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SafeGraphPlaceStore {

    private static SafeGraphPlaceStore sharedInstance = null;

    public static SafeGraphPlaceStore sharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new SafeGraphPlaceStore();
        }

        return sharedInstance;
    }

   public void fetchNearbyPlaces(OpenLocateLocation openLocateLocation, final SafeGraphPlaceCallback callback) {
       SafeGraphPlaceClient safeGraphPlaceClient = ClientGenerator.createClient(SafeGraphPlaceClient.class);
       Call<SafeGraphPlaceBody> call=safeGraphPlaceClient.getAllPlaces(getQueryMap(openLocateLocation));

       call.enqueue(new Callback<SafeGraphPlaceBody>() {
           @Override
           public void onResponse(Call<SafeGraphPlaceBody> call, Response<SafeGraphPlaceBody> response) {
               if(response.isSuccessful()) {
                   List<SafeGraphPlace> places = response.body().getPlaceList();
                   if (places != null && !places.isEmpty()) {
                       callback.onSuccess(places);
                   } else {
                       callback.onFailure(new Error(
                               "No nearby places found"
                       ));
                   }
               }
           }

           @Override
           public void onFailure(Call<SafeGraphPlaceBody> call, Throwable t) {
               callback.onFailure(new Error(t));
           }
       });
    }

    private Map<String, String> getQueryMap(OpenLocateLocation location ) {
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("advertising_id", location.getAdvertisingId());
        queryMap.put("advertising_id_type", "aaid");
        queryMap.put("latitude", String.valueOf(location.getLatitude()));
        queryMap.put("longitude", String.valueOf(location.getLongitude()));
        queryMap.put("horizontal_accuracy", String.valueOf(location.getHorizontalAccuracy()));

        return queryMap;
    }
}
