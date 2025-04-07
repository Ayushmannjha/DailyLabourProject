package com.dailyWorker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="application_status")
public class ApplicationStatus {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;
private int workId;
private int workerId;
private int status;
}
