package com.subu.stjosephs.tollpay.Objects;

public class User {


    private String name;
    private String mobile_number;
    private String email_id;

    public User() {

    }

    public User(String name, String mobile_number, String email_id) {
        this.name = name;
        this.mobile_number = mobile_number;
        this.email_id = email_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }
}




