package com.subu.stjosephs.tollpay.Objects;

public class Vehicles_Entry
{
    private String uid;
    private String vehicle_number;
    private String key;

    public Vehicles_Entry()
    {

    }

    public Vehicles_Entry(String uid, String vehicle_number, String key) {
        this.uid = uid;
        this.vehicle_number = vehicle_number;
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
