package com.dailyWorker.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="workers")
@Setter
@Getter
public class Workers {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;
private String name;
private String email;
private String phone;
private String role;
private String password;
private String workPlaces;
private int avability;
private String state;
private String city;
private String locality;
private String pincode;
private double wallet;
}
