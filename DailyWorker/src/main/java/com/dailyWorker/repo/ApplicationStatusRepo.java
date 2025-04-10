package com.dailyWorker.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.ApplicationStatus;

@Repository
public interface ApplicationStatusRepo extends JpaRepository<ApplicationStatus, Integer>{
List<ApplicationStatus> findByWorkId(int workId);

List<ApplicationStatus> findByWorkerId(int workerId);

ApplicationStatus findByWorkerIdAndWorkId(int workerId, int workId);

ApplicationStatus findById(int id);

@Query("SELECT a.workerId FROM ApplicationStatus a WHERE a.workId = :workId AND a.status = :status")
List<Integer> findWorkerIdsByWorkId(@Param("workId") int workId, @Param("status")int status);
}
