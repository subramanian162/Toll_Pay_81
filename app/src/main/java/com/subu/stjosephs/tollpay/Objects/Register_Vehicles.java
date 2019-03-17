package com.subu.stjosephs.tollpay.Objects;

public class Register_Vehicles {

    private String u_vehicle_number;
    private String u_vehicle_type;
    private String u_key;

    Register_Vehicles()
    {

    }

    public Register_Vehicles(String u_vehicle_number, String u_vehicle_type, String u_key) {
        this.u_vehicle_number = u_vehicle_number;
        this.u_vehicle_type = u_vehicle_type;
        this.u_key = u_key;
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

    public String getU_key() {
        return u_key;
    }

    public void setU_key(String u_key) {
        this.u_key = u_key;
    }
}
