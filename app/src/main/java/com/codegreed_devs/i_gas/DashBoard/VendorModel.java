package com.codegreed_devs.i_gas.DashBoard;

public class VendorModel {

    private String vendorId;
    private String vendorName;
    private String vendorAddress;
    private String fcmToken;
    private int vendorDistance;

    public VendorModel(String vendorId, String vendorName, String vendorAddress, String fcmToken, int vendorDistance) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.vendorAddress = vendorAddress;
        this.fcmToken = fcmToken;
        this.vendorDistance = vendorDistance;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public int getVendorDistance() {
        return vendorDistance;
    }
}
