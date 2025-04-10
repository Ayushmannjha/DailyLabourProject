package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.WebUser;



@Repository
public interface WebUserRepo extends JpaRepository<WebUser, Integer>{
WebUser findByEmail(String email);
WebUser findById(int id);
}
