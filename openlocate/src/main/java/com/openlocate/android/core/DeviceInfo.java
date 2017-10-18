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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

final class DeviceInfo {
    private String manufacturer;
    private String model;
    private String operatingSystem;
    private boolean isCharging;

    private static final String BASE_NAME = "Android";

    private DeviceInfo(Context context) {
        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;
        operatingSystem = BASE_NAME + " " + Build.VERSION.RELEASE;

        Intent batteryIntent = context.registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );
        isCharging = isCharging(batteryIntent);
    }

    public static DeviceInfo from(Context context) {
        return new DeviceInfo(context);
    }

    public static DeviceInfo from(String manufacturer, String model, String operatingSystem, boolean isCharging) {
        return new DeviceInfo(manufacturer, model, operatingSystem, isCharging);
    }

    private DeviceInfo(String manufacturer, String model, String operatingSystem, boolean isCharging) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.operatingSystem = operatingSystem;
        this.isCharging = isCharging;
    }

    String getManufacturer() {
        return manufacturer;
    }

    String getModel() {
        return model;
    }

    String getOperatingSystem() {
        return operatingSystem;
    }

    boolean isCharging() {
        return isCharging;
    }

    private boolean isCharging(Intent batteryIntent) {
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
        return status == BatteryManager.BATTERY_STATUS_CHARGING;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", isCharging=" + isCharging +
                '}';
    }
}
