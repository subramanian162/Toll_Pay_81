package com.subu.stjosephs.tollpay.Objects;

public class User_C_Vehicles {
    private String date;
    private String time;
    private String vehicle_number;
    private String Amount;
    private String Status;
    public User_C_Vehicles(){

    }

    public User_C_Vehicles(String date, String time, String vehicle_number, String amount, String status) {
        this.date = date;
        this.time = time;
        this.vehicle_number = vehicle_number;
        Amount = amount;
        Status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
