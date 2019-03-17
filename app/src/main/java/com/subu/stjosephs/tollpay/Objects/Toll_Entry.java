package com.subu.stjosephs.tollpay.Objects;

public class Toll_Entry {

    private String toll_crossed_vehicle_from_camera;
    private String payment_status;
    private String toll_entry_key;

    public Toll_Entry()
    {

    }

    public Toll_Entry(String toll_crossed_vehicle_from_camera, String payment_status, String toll_entry_key) {
        this.toll_crossed_vehicle_from_camera = toll_crossed_vehicle_from_camera;
        this.payment_status = payment_status;
        this.toll_entry_key = toll_entry_key;
    }

    public String getToll_crossed_vehicle_from_camer() {
        return toll_crossed_vehicle_from_camera;
    }

    public void setToll_crossed_vehicle_from_camer(String toll_crossed_vehicle_from_camer) {
        this.toll_crossed_vehicle_from_camera = toll_crossed_vehicle_from_camer;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getToll_entry_key() {
        return toll_entry_key;
    }

    public void setToll_entry_key(String toll_entry_key) {
        this.toll_entry_key = toll_entry_key;
    }
}
