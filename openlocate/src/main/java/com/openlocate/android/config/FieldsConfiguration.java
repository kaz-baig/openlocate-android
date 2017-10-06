package com.openlocate.android.config;

public class FieldsConfiguration {

    private boolean wifiInfo;

    private boolean carrierName;
    private boolean connectionType;

    private boolean locationMethod;
    private boolean locationContext;

    private boolean deviceManufacturer;
    private boolean deviceModel;
    private boolean operatingSystem;

    private boolean course;
    private boolean speed;
    private boolean altitude;

    public FieldsConfiguration(boolean wifiInfo, boolean carrierName, boolean connectionType, boolean locationMethod, boolean locationContext, boolean deviceManufacturer, boolean deviceModel, boolean operatingSystem, boolean course, boolean speed, boolean altitude) {
        this.wifiInfo = wifiInfo;
        this.carrierName = carrierName;
        this.connectionType = connectionType;
        this.locationMethod = locationMethod;
        this.locationContext = locationContext;
        this.deviceManufacturer = deviceManufacturer;
        this.deviceModel = deviceModel;
        this.operatingSystem = operatingSystem;
        this.course = course;
        this.speed = speed;
        this.altitude = altitude;
    }
}
