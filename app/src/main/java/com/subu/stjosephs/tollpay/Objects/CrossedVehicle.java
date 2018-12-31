package com.subu.stjosephs.tollpay.Objects;

public class CrossedVehicle {

    private String crossed_vehicle_number;
    private String getCrossed_vehicle_amount;

    public CrossedVehicle(){

    }

    public CrossedVehicle(String crossed_vehicle_number, String getCrossed_vehicle_amount) {
        this.crossed_vehicle_number = crossed_vehicle_number;
        this.getCrossed_vehicle_amount = getCrossed_vehicle_amount;
    }

    public String getCrossed_vehicle_number() {
        return crossed_vehicle_number;
    }

    public void setCrossed_vehicle_number(String crossed_vehicle_number) {
        this.crossed_vehicle_number = crossed_vehicle_number;
    }

    public String getGetCrossed_vehicle_amount() {
        return getCrossed_vehicle_amount;
    }

    public void setGetCrossed_vehicle_amount(String getCrossed_vehicle_amount) {
        this.getCrossed_vehicle_amount = getCrossed_vehicle_amount;
    }
}
