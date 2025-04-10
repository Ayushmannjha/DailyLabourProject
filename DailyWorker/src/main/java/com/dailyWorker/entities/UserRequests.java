package com.dailyWorker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="user_requests")
@Setter
@Getter
public class UserRequests {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;

private int userId;
private int workerId;
private double budget;
private int status;
private int workId;
}
