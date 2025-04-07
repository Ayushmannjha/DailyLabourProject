package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.Verification;



@Repository
public interface VerificationRepo extends JpaRepository<Verification, Integer>{
Verification findByWorkerId(int workerId);
}
