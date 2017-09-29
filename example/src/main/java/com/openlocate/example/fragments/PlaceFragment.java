
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
package com.openlocate.example.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.openlocate.android.callbacks.OpenLocateLocationCallback;
import com.openlocate.android.core.OpenLocate;
import com.openlocate.android.core.OpenLocateLocation;
import com.openlocate.android.exceptions.LocationDisabledException;
import com.openlocate.android.exceptions.LocationPermissionException;
import com.openlocate.example.R;
import com.openlocate.example.adapters.SafeGraphLocationListAdapter;
import com.openlocate.example.callbacks.SafeGraphPlaceCallback;
import com.openlocate.example.models.SafeGraphPlace;
import com.openlocate.example.stores.SafeGraphPlaceStore;

import java.util.ArrayList;
import java.util.List;

public class PlaceFragment extends Fragment {

    public List<SafeGraphPlace> places;

    private RecyclerView recyclerView;
    private TextView fetchLocation;
    private SafeGraphLocationListAdapter safeGraphLocationListAdapter;
    private Activity activity;
    private ProgressDialog progress;

    public static PlaceFragment getInstance() {
        return new PlaceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_list, null);
        places = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        fetchLocation = (TextView) view.findViewById(R.id.txt_fetch_location);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.app_name);
    }

    public void currentPlace() {
        try {
            OpenLocate openLocate = OpenLocate.getInstance(activity);
            showDialog();
            openLocate.getCurrentLocation(new OpenLocateLocationCallback() {
                @Override
                public void onLocationFetch(OpenLocateLocation location) {
                    onFetchCurrentLocation(location);
                }

                @Override
                public void onError(Error error) {
                    showToast(error.getLocalizedMessage());
                    dismissDialog();
                }
            });
        } catch (LocationDisabledException | LocationPermissionException e) {
            showToast(e.getLocalizedMessage());
            dismissDialog();
        }
    }

    private void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    private void onFetchCurrentLocation(OpenLocateLocation location) {
        SafeGraphPlaceStore.sharedInstance().fetchNearbyPlaces(location, new SafeGraphPlaceCallback() {
            @Override
            public void onSuccess(List<SafeGraphPlace> places) {
                dismissDialog();
                fetchLocation.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                safeGraphLocationListAdapter = new SafeGraphLocationListAdapter(PlaceFragment.this.places);
                recyclerView.setAdapter(safeGraphLocationListAdapter);
            }

            @Override
            public void onFailure(Error error) {
                showToast(error.getLocalizedMessage());
                dismissDialog();
            }
        });
    }

    public void showDialog() {
        progress = ProgressDialog.show(activity, getString(R.string.loading),
                getString(R.string.please_wait), true);
    }

    public void dismissDialog() {
        if(progress!=null) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
        }
    }
}
