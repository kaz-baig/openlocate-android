package com.openlocate.android.core;

import android.location.Location;

import com.openlocate.android.exceptions.GooglePlayServicesNotAvailable;
import com.openlocate.android.exceptions.InvalidConfigurationException;
import com.openlocate.android.exceptions.LocationDisabledException;
import com.openlocate.android.exceptions.LocationPermissionException;

import java.util.List;

public interface OpenLocateLocationTransmitter extends OpenLocateLocationTracker {

    void addLocation(Location location) throws InvalidConfigurationException, LocationDisabledException, LocationPermissionException, GooglePlayServicesNotAvailable;

    void addLocations(List<Location> locations) throws InvalidConfigurationException, LocationDisabledException, LocationPermissionException, GooglePlayServicesNotAvailable;

}
