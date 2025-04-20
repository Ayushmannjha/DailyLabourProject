package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.ReadNotificationUserReq;
import java.util.List;


@Repository
public interface ReadNotificationUserReqRepo extends JpaRepository<ReadNotificationUserReq, Integer>{
List<ReadNotificationUserReq> findByWorkerId(int workerId);
}
