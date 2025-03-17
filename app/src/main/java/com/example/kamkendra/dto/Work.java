package com.example.kamkendra.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Work {

    private int id;
    private String city;
    private String state;
    private String pincode;
    private String phone;
    private String ownerName;
    private String laboursDetails;
    private String description;
    private double budget;
    private int userId;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLaboursDetails(String laboursDetails) {
        this.laboursDetails = laboursDetails;
    }

    public String getLaboursDetails() {
        return laboursDetails;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPincode() {
        return pincode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
