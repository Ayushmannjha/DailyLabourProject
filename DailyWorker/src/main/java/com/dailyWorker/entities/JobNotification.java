package com.dailyWorker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="job_notifications")
@Setter
@Getter
public class JobNotification {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;
private String msg;
private String title;

}
