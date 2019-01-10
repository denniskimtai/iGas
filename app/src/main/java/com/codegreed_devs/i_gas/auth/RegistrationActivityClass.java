package com.codegreed_devs.i_gas.auth;

public class RegistrationActivityClass {

    public String clientId;
    public String clientName;
    public String clientEmail;
    public String regPhoneNumber;
    public String regLocation;

    public RegistrationActivityClass(String clientId, String clientName, String clientEmail, String regPhoneNumber, String regLocation) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.regPhoneNumber = regPhoneNumber;
        this.regLocation = regLocation;
    }
}
