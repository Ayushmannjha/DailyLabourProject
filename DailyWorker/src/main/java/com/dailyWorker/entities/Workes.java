package com.dailyWorker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Workes")
@Setter
@Getter
public class Workes {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
