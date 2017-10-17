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
package com.safegraph.transmitonlyexample.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.openlocate.android.config.Configuration;
import com.openlocate.android.core.OpenLocate;
import com.openlocate.android.exceptions.InvalidConfigurationException;
import com.openlocate.android.exceptions.LocationDisabledException;
import com.openlocate.android.exceptions.LocationPermissionException;
import com.safegraph.transmitonlyexample.BuildConfig;
import com.safegraph.transmitonlyexample.R;
import com.safegraph.transmitonlyexample.activities.MainActivity;

import java.util.HashMap;

public class TrackFragment extends Fragment {

    private static final int LOCATION_PERMISSION = 1001;
    private static String TAG = MainActivity.class.getSimpleName();

    private Activity activity;
    private Button startButton;
    private Button stopButton;
    private Button addButton;
    private OpenLocate openLocate;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    public static TrackFragment getInstance() {
        return new TrackFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    private void createLocationRequest() {

        if (openLocate != null && openLocate.isTracking()) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            initLocationRequestCallback();

            try {
                mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
            } catch (SecurityException e) {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION
                );
            }
        } else {
            Toast.makeText(getActivity(), "Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private LocationRequest getLocationRequest() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void initLocationRequestCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    if (OpenLocate.getTransmitOnlyInstance(activity).isTracking()) {
                        addLocationToSDK(location);
                        Toast.makeText(activity, "Location added manually", Toast.LENGTH_SHORT).show();
                    }
                    mFusedLocationClient.removeLocationUpdates(this);
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_track, null);
        startButton = (Button) view.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });

        stopButton = (Button) view.findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTracking();
            }
        });

        addButton = (Button) view.findViewById(R.id.add_loc);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLocationRequest();
            }
        });

        OpenLocate openLocate = OpenLocate.getTransmitOnlyInstance(activity);
        if (openLocate.isTracking()) {
            onStartService();
        }
        return view;
    }

    private void addLocationToSDK(Location location) {
        if (openLocate != null && OpenLocate.getTransmitOnlyInstance(activity).isTracking()) {
            try {
                openLocate.addLocation(location);
                Log.d(TAG, "Location added manually.");
            } catch (LocationDisabledException e) {
                Log.d(TAG, "addLocationToSDK: " + e.getMessage());
            } catch (LocationPermissionException e) {
                Log.d(TAG, "addLocationToSDK: " + e.getMessage());
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.app_name);
    }

    private void startTracking() {

        try {

            Configuration configuration = new Configuration.Builder()
                    .setUrl(BuildConfig.URL)
                    .setHeaders(getHeader())
                    .build();

            openLocate = OpenLocate.getTransmitOnlyInstance(activity);
            openLocate.startTracking(configuration);

            Toast.makeText(activity, getString(R.string.sercive_started), Toast.LENGTH_LONG).show();
            onStartService();
        } catch (InvalidConfigurationException | LocationDisabledException e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        } catch (LocationPermissionException e) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION
            );
        }
    }

    private HashMap<String, String> getHeader() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + BuildConfig.TOKEN);

        return headers;
    }

    private void stopTracking() {
        OpenLocate.getInstance(activity).stopTracking();
        Toast.makeText(activity, getString(R.string.sercive_stopped), Toast.LENGTH_LONG).show();
        onStopService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION:
                onLocationRequestResult(grantResults);
                break;
        }
    }

    private void onLocationRequestResult(@NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTracking();
        }
    }

    private void onStartService() {
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.VISIBLE);
    }

    private void onStopService() {
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
        addButton.setVisibility(View.INVISIBLE);
    }
}
