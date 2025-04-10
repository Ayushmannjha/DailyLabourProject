package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dailyWorker.entities.LoginCredential;



public interface LoginCredendialRepo extends JpaRepository<LoginCredential, Integer>{

	LoginCredential findByEmail(String email);
	LoginCredential findByPhone(String phone);
	LoginCredential findById(int id);
}
