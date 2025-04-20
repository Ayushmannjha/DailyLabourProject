package com.dailyWorker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "read_notification_user_req")
@Setter
@Getter
public class ReadNotificationUserReq {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;
private int userReqId;
private int workerId;
private int isRead;
}
