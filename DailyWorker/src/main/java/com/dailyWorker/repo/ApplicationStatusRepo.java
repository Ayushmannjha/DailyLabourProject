package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dailyWorker.entities.ApplicationStatus;
import java.util.List;


public interface ApplicationStatusRepo extends JpaRepository<ApplicationStatus, Integer>{
List<ApplicationStatus> findByWorkId(int workId);

List<ApplicationStatus> findByWorkerId(int workerId);

ApplicationStatus findByWorkerIdAndAndWorkId(int workerId, int workId);

ApplicationStatus findById(int id);
}
