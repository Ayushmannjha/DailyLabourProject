package com.dailyWorker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "read_notification")
@Setter
@Getter
public class ReadNotification {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;
private int jobNotificationId;
private int workerId;
private int isRead;
private String category;
}
