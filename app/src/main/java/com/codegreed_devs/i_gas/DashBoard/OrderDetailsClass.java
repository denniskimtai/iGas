package com.codegreed_devs.i_gas.DashBoard;

public class OrderDetailsClass {
    public String gasSize;
    public String gasType;
    public String mnumberOfCylinders;
    public String gasBrand;


    public OrderDetailsClass(String mgasSize, String mgasType, String mnumberOfCylinders, String mGasBrand) {
        this.gasSize = mgasSize;
        this.gasType = mgasType;
        this.mnumberOfCylinders = mnumberOfCylinders;
        this.gasBrand = mGasBrand;
    }
}
