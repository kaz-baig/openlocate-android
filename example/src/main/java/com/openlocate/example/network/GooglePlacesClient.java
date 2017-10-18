package com.openlocate.example.network;

import com.openlocate.example.models.places.GooglePlaceBody;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface GooglePlacesClient {

    @GET("places/v1/nearby")
    Call<GooglePlaceBody>
    getNearbyPlacesFromGoogle(@QueryMap Map<String, String> queryMap);
}
