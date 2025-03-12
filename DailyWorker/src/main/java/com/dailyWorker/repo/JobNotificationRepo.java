package com.dailyWorker.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.JobNotification;
@Repository
public interface JobNotificationRepo extends JpaRepository<JobNotification, Integer>{
List<JobNotification> findAll();


}
