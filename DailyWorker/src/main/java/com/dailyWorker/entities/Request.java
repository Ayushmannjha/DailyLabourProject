package com.dailyWorker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Request {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;
private int workerId;
private int userId;
private int status;

}
