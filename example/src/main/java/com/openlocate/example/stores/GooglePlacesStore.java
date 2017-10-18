package com.openlocate.example.stores;

import android.util.Log;

import com.openlocate.android.core.OpenLocateLocation;
import com.openlocate.example.callbacks.GetPlacesCallback;
import com.openlocate.example.models.places.GooglePlaceBody;
import com.openlocate.example.network.GooglePlacesClient;
import com.openlocate.example.network.GooglePlacesClientGenerator;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlacesStore {

    public static final String TAG = GooglePlacesStore.class.getSimpleName();

    public static GooglePlacesStore sharedInstance = null;

    public static GooglePlacesStore sharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new GooglePlacesStore();
        }

        return sharedInstance;
    }

    private Map<String, String> getQueryMapForGoogle(OpenLocateLocation location ) {
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("location", String.valueOf(location.getLocation().getLatitude()) + "," + String.valueOf(location.getLocation().getLongitude()) );
        queryMap.put("radius", "500");
        queryMap.put("type", "restaurant");
        queryMap.put("keyword", "south");
        queryMap.put("key", "");

        return queryMap;
    }

    public void fetchGooglePlaces(OpenLocateLocation openLocateLocation, final GetPlacesCallback callback) {

        GooglePlacesClient safeGraphPlaceClient = GooglePlacesClientGenerator.createClient(GooglePlacesClient.class);
        Call<GooglePlaceBody> call=safeGraphPlaceClient.getNearbyPlacesFromGoogle(getQueryMapForGoogle(openLocateLocation));

        call.enqueue(new Callback<GooglePlaceBody>() {
            @Override
            public void onResponse(Call<GooglePlaceBody> call, Response<GooglePlaceBody> response) {
                if(response.isSuccessful()) {
                    Log.i(TAG, "onResponse: Great Success");
                }
            }

            @Override
            public void onFailure(Call<GooglePlaceBody> call, Throwable t) {
                Log.i(TAG, "onFailure: Failed");
            }
        });
    }


}
