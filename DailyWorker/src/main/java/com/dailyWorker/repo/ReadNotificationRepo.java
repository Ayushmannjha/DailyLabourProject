package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.ReadNotification;
import java.util.List;

@Repository
public interface ReadNotificationRepo extends JpaRepository<ReadNotification, Integer>{
List<ReadNotification> findByWorkerIdAndIsRead(int workerId, int isRead);
List<ReadNotification> findByWorkerId(int workerId);
}
