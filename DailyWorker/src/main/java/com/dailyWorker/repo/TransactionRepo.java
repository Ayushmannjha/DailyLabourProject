package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.Transactions;
import java.util.List;
import java.util.Optional;


@Repository
public interface TransactionRepo extends JpaRepository<Transactions, Integer>{
List<Transactions> findByWorkerId(int workerId);
Optional<Transactions> findByTxId(String txId);
}
