package com.subu.stjosephs.tollpay.Objects;

public class UserVehicle {

    private String u_name;
    private String u_vehicle_number;
    private String u_vehicle_type;
    private int u_phone_number;
    private int u_amount;

    public UserVehicle()
    {

    }

    public UserVehicle(String u_name, String u_vehicle_number, String u_vehicle_type, int u_phone_number, int u_amount) {
        this.u_name = u_name;
        this.u_vehicle_number = u_vehicle_number;
        this.u_vehicle_type = u_vehicle_type;
        this.u_phone_number = u_phone_number;
        this.u_amount = u_amount;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getU_vehicle_number() {
        return u_vehicle_number;
    }

    public void setU_vehicle_number(String u_vehicle_number) {
        this.u_vehicle_number = u_vehicle_number;
    }

    public String getU_vehicle_type() {
        return u_vehicle_type;
    }

    public void setU_vehicle_type(String u_vehicle_type) {
        this.u_vehicle_type = u_vehicle_type;
    }

    public int getU_phone_number() {
        return u_phone_number;
    }

    public void setU_phone_number(int u_phone_number) {
        this.u_phone_number = u_phone_number;
    }

    public int getU_amount() {
        return u_amount;
    }

    public void setU_amount(int u_amount) {
        this.u_amount = u_amount;
    }
}
