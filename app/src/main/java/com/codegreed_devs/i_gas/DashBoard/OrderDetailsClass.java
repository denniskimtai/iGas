package com.codegreed_devs.i_gas.DashBoard;

public class OrderDetailsClass {
    public String gasSize;
    public String gasType;
    public String numberOfCylinders;
    public String gasBrand;
    public String clientId;
    public String orderId;


    public OrderDetailsClass(String mgasSize, String mgasType, String mnumberOfCylinders, String mGasBrand, String mClientId, String mOrderId) {
        this.gasSize = mgasSize;
        this.gasType = mgasType;
        this.numberOfCylinders = mnumberOfCylinders;
        this.gasBrand = mGasBrand;
        this.clientId = mClientId;
        this.orderId = mOrderId;
    }
}
