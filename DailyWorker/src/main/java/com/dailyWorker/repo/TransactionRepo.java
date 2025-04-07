package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.Transactions;
import java.util.List;


@Repository
public interface TransactionRepo extends JpaRepository<Transactions, Integer>{
List<Transactions> findByWorkerId(int workerId);
}
