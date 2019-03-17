package com.subu.stjosephs.tollpay.Objects;

public class User_R_Vehicles {

    private String u_vehicle_number;
    private String u_vehicle_type;

    public User_R_Vehicles() {

    }

    public User_R_Vehicles(String u_vehicle_number, String u_vehicle_type) {
        this.u_vehicle_number = u_vehicle_number;
        this.u_vehicle_type = u_vehicle_type;
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
}
