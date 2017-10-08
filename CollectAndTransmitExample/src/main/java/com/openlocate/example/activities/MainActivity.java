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
package com.openlocate.example.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.openlocate.example.R;
import com.openlocate.example.fragments.PlaceFragment;
import com.openlocate.example.fragments.TrackFragment;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private boolean showReloadPlacesButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        initializeBottomNavigationView();
    }

    private void initializeBottomNavigationView() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                TrackFragment.getInstance()
        ).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_track:
                    showReloadPlacesButton = false;
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.fragment_container,
                            TrackFragment.getInstance()
                    ).commit();

                    invalidateOptionsMenu();
                    return true;
                case R.id.navigation_places:
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.fragment_container,
                            PlaceFragment.getInstance()
                    ).commit();

                    showReloadPlacesButton = true;
                    invalidateOptionsMenu();
                    return true;
            }
            return false;
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showReloadPlacesButton) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            final MenuItem myActionMenuItem = menu.findItem(R.id.action_refresh);
            myActionMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    PlaceFragment placeFragment = (PlaceFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (placeFragment.isAdded()) {
                        placeFragment.currentPlace();
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }
}
