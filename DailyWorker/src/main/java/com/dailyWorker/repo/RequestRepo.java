package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dailyWorker.entities.Request;
import java.util.List;


public interface RequestRepo extends JpaRepository<Request, Integer>{
List<Request> findByUserIdAndAndWorkerId(int userId, int workerId);
List<Request> findByStatus(int status);
List<Request> findByWorkerId(int workerId);

}
