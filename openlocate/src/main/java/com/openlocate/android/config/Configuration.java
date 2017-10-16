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
package com.openlocate.android.config;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Configuration implements Parcelable {

    private String url;
    private HashMap<String, String> headers;

    private boolean isWifiCollectionDisabled;
    private boolean isDeviceModelCollectionDisabled;
    private boolean isDeviceManufacturerCollectionDisabled;
    private boolean isOperatingSystemCollectionDisbaled;
    private boolean isChargingInfoCollectionDisabled;
    private boolean isCarrierNameCollectionDisabled;
    private boolean isConnectionTypeCollectionDisabled;
    private boolean isLocationMethodCollectionDisabled;
    private boolean isLocationContextCollectionDisabled;

    public static class Builder {

        private String url;
        private Map<String, String> headers;
        private boolean isWifiCollectionDisabled;
        private boolean isDeviceModelCollectionDisabled;
        private boolean isDeviceManufacturerCollectionDisabled;
        private boolean isOperatingSystemCollectionDisbaled;
        private boolean isChargingInfoCollectionDisabled;
        private boolean isCarrierNameCollectionDisabled;
        private boolean isConnectionTypeCollectionDisabled;
        private boolean isLocationMethodCollectionDisabled;
        private boolean isLocationContextCollectionDisabled;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Configuration build() {
            if (headers == null) {
                return new Configuration(url, new HashMap<String, String>(),
                        isWifiCollectionDisabled, isDeviceModelCollectionDisabled, isDeviceManufacturerCollectionDisabled,
                        isOperatingSystemCollectionDisbaled, isChargingInfoCollectionDisabled, isCarrierNameCollectionDisabled,
                        isConnectionTypeCollectionDisabled, isLocationMethodCollectionDisabled, isLocationContextCollectionDisabled);
            } else {
                return new Configuration(url, new HashMap<>(headers),
                        isWifiCollectionDisabled, isDeviceModelCollectionDisabled, isDeviceManufacturerCollectionDisabled,
                        isOperatingSystemCollectionDisbaled, isChargingInfoCollectionDisabled, isCarrierNameCollectionDisabled,
                        isConnectionTypeCollectionDisabled, isLocationMethodCollectionDisabled, isLocationContextCollectionDisabled);
            }
        }

        public Builder withoutWifiInfo() {
            this.isWifiCollectionDisabled = true;
            return this;
        }

        public Builder withoutDeviceModel() {
            this.isDeviceModelCollectionDisabled = true;
            return this;
        }

        public Builder withoutDeviceManufacturer() {
            this.isDeviceManufacturerCollectionDisabled = true;
            return this;
        }

        public Builder withoutOperatingSystem() {
            this.isOperatingSystemCollectionDisbaled = true;
            return this;
        }

        public Builder withoutChargingInfo() {
            this.isChargingInfoCollectionDisabled = true;
            return this;
        }

        public Builder withoutCarrierName() {
            this.isCarrierNameCollectionDisabled = true;
            return this;
        }

        public Builder withoutConnectionType() {
            this.isConnectionTypeCollectionDisabled = true;
            return this;
        }

        public Builder withoutLocationMethod() {
            this.isLocationMethodCollectionDisabled = true;
            return this;
        }

        public Builder withoutLocationContext() {
            this.isLocationContextCollectionDisabled = true;
            return this;
        }
    }

    private Configuration(String url, HashMap<String, String> headers,
                          boolean isWifiCollectionDisabled, boolean isDeviceModelCollectionDisabled, boolean isDeviceManufacturerCollectionDisabled,
                          boolean isOperaringSystemCollectionDisbaled, boolean isChargingInfoCollectionDisabled, boolean isCarrierNameCollectionDisabled,
                          boolean isConnectionTypeCollectionDisabled, boolean isLocationMethodCollectionDisabled, boolean isLocationContextCollectionDisabled) {
        this.url = url;
        this.headers = headers;
        this.isWifiCollectionDisabled = isWifiCollectionDisabled;
        this.isDeviceModelCollectionDisabled = isDeviceModelCollectionDisabled;
        this.isDeviceManufacturerCollectionDisabled = isDeviceManufacturerCollectionDisabled;
        this.isOperatingSystemCollectionDisbaled = isOperaringSystemCollectionDisbaled;
        this.isChargingInfoCollectionDisabled = isChargingInfoCollectionDisabled;
        this.isCarrierNameCollectionDisabled = isCarrierNameCollectionDisabled;
        this.isConnectionTypeCollectionDisabled = isConnectionTypeCollectionDisabled;
        this.isLocationMethodCollectionDisabled = isLocationMethodCollectionDisabled;
        this.isLocationContextCollectionDisabled = isLocationContextCollectionDisabled;
    }

    public String getUrl() {
        return url;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public boolean isValid() {
        return getUrl() != null && !getUrl().isEmpty();
    }

    public boolean isWifiCollectionDisabled() {
        return isWifiCollectionDisabled;
    }

    public boolean isDeviceModelCollectionDisabled() {
        return isDeviceModelCollectionDisabled;
    }

    public boolean isDeviceManufacturerCollectionDisabled() {
        return isDeviceManufacturerCollectionDisabled;
    }

    public boolean isOperaringSystemCollectionDisbaled() {
        return isOperatingSystemCollectionDisbaled;
    }

    public boolean isChargingInfoCollectionDisabled() {
        return isChargingInfoCollectionDisabled;
    }

    public boolean isCarrierNameCollectionDisabled() {
        return isCarrierNameCollectionDisabled;
    }

    public boolean isConnectionTypeCollectionDisabled() {
        return isConnectionTypeCollectionDisabled;
    }

    public boolean isLocationMethodCollectionDisabled() {
        return isLocationMethodCollectionDisabled;
    }

    public boolean isLocationContextCollectionDisabled() {
        return isLocationContextCollectionDisabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeSerializable(this.headers);
        dest.writeByte(this.isWifiCollectionDisabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDeviceModelCollectionDisabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDeviceManufacturerCollectionDisabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isOperatingSystemCollectionDisbaled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isChargingInfoCollectionDisabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCarrierNameCollectionDisabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isConnectionTypeCollectionDisabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLocationMethodCollectionDisabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLocationContextCollectionDisabled ? (byte) 1 : (byte) 0);
    }

    protected Configuration(Parcel in) {
        this.url = in.readString();
        this.headers = (HashMap<String, String>) in.readSerializable();
        this.isWifiCollectionDisabled = in.readByte() != 0;
        this.isDeviceModelCollectionDisabled = in.readByte() != 0;
        this.isDeviceManufacturerCollectionDisabled = in.readByte() != 0;
        this.isOperatingSystemCollectionDisbaled = in.readByte() != 0;
        this.isChargingInfoCollectionDisabled = in.readByte() != 0;
        this.isCarrierNameCollectionDisabled = in.readByte() != 0;
        this.isConnectionTypeCollectionDisabled = in.readByte() != 0;
        this.isLocationMethodCollectionDisabled = in.readByte() != 0;
        this.isLocationContextCollectionDisabled = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Configuration> CREATOR = new Parcelable.Creator<Configuration>() {
        @Override
        public Configuration createFromParcel(Parcel source) {
            return new Configuration(source);
        }

        @Override
        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };
}
