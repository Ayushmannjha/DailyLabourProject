package com.dailyWorker.dao;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionRequest {
private int id;
private int workerId;
private int userId;
private String phone;
private double amount;
private String name;
private String email;
}
