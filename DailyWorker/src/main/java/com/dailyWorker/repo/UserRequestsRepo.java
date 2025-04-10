package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dailyWorker.entities.UserRequests;
import java.util.List;
import java.util.Optional;


public interface UserRequestsRepo extends JpaRepository<UserRequests, Integer>{
List<UserRequests> findByUserId(int userId);
List<UserRequests> findByWorkerId(int workerId);
UserRequests findById(int id) ;
UserRequests findByWorkIdAndWorkerId(int workId, int workerId);
}
