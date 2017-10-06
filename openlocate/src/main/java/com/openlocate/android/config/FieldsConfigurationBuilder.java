package com.openlocate.android.config;

public class FieldsConfigurationBuilder {
    private boolean wifiInfo = true;
    private boolean carrierName = true;
    private boolean connectionType = true;
    private boolean locationMethod = true;
    private boolean locationContext = true;
    private boolean deviceManufacturer = true;
    private boolean deviceModel = true;
    private boolean operatingSystem = true;
    private boolean course = true;
    private boolean speed = true;
    private boolean altitude = true;

    public FieldsConfigurationBuilder collectWifiInfo(boolean wifiInfo) {
        this.wifiInfo = wifiInfo;
        return this;
    }

    public FieldsConfigurationBuilder collectCarrierName(boolean carrierName) {
        this.carrierName = carrierName;
        return this;
    }

    public FieldsConfigurationBuilder collectConnectionType(boolean connectionType) {
        this.connectionType = connectionType;
        return this;
    }

    public FieldsConfigurationBuilder collectLocationMethod(boolean locationMethod) {
        this.locationMethod = locationMethod;
        return this;
    }

    public FieldsConfigurationBuilder collectLocationContext(boolean locationContext) {
        this.locationContext = locationContext;
        return this;
    }

    public FieldsConfigurationBuilder collectDeviceManufacturer(boolean deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
        return this;
    }

    public FieldsConfigurationBuilder collectDeviceModel(boolean deviceModel) {
        this.deviceModel = deviceModel;
        return this;
    }

    public FieldsConfigurationBuilder collectOperatingSystem(boolean operatingSystem) {
        this.operatingSystem = operatingSystem;
        return this;
    }

    public FieldsConfigurationBuilder collectCourse(boolean course) {
        this.course = course;
        return this;
    }

    public FieldsConfigurationBuilder collectSpeed(boolean speed) {
        this.speed = speed;
        return this;
    }

    public FieldsConfigurationBuilder collectAltitude(boolean altitude) {
        this.altitude = altitude;
        return this;
    }

    public FieldsConfiguration build() {
        return new FieldsConfiguration(wifiInfo, carrierName, connectionType, locationMethod, locationContext, deviceManufacturer, deviceModel, operatingSystem, course, speed, altitude);
    }
}