package com.codegreed_devs.i_gas;

public class OrderHistoryModel {
    private String gasBrand, gasSize, gasType, numberOfCylinders, orderId, orderStatus;

    public OrderHistoryModel(String mgasBrand, String gasSize, String gasType, String numberOfCylinders, String orderId, String orderStatus){
        this.gasBrand = mgasBrand;
        this.gasSize = gasSize;
        this.gasType = gasType;
        this.numberOfCylinders = numberOfCylinders;
        this.orderId = orderId;
        this.orderStatus = orderStatus;

    }

    public String getGasBrand() {
        return gasBrand;
    }

    public void setGasBrand(String gasBrand) {
        this.gasBrand = gasBrand;
    }

    public String getGasSize() {
        return gasSize;
    }

    public void setGasSize(String gasSize) {
        this.gasSize = gasSize;
    }

    public String getGasType() {
        return gasType;
    }

    public void setGasType(String gasType) {
        this.gasType = gasType;
    }

    public String getNumberOfCylinders() {
        return numberOfCylinders;
    }

    public void setNumberOfCylinders(String numberOfCylinders) {
        this.numberOfCylinders = numberOfCylinders;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
