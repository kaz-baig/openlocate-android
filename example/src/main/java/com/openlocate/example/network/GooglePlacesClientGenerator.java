package com.openlocate.example.network;

import com.openlocate.example.utils.Configuration;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GooglePlacesClientGenerator {

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Configuration.GOOGLE_BASE_URL)
            .client(
                    new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .addInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request original = chain.request();
                                    Request.Builder builder = original.newBuilder();
                                    builder.addHeader("Content-Type", "application/json");
                                    return chain.proceed(builder.build());
                                }
                            })
                            .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <S> S createClient(
            Class<S> clientClass) {
        return retrofit.create(clientClass);
    }
}
