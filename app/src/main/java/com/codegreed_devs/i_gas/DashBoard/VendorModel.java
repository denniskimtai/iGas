package com.codegreed_devs.i_gas.DashBoard;

public class VendorModel {

    private String vendorId;
    private String vendorName;
    private String vendorAddress;
    private String fcmToken;

    public VendorModel(String vendorId, String vendorName, String vendorAddress, String fcmToken) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.vendorAddress = vendorAddress;
        this.fcmToken = fcmToken;
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
}
