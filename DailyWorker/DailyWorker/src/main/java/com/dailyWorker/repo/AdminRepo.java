package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.Admin;
import java.util.List;



@Repository
public interface AdminRepo extends JpaRepository<Admin, Integer>{
Admin findByEmailAndPassword(String email, String password);
Admin findByEmail(String email);
}
