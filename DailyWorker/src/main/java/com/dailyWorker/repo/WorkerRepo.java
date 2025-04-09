package com.dailyWorker.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.Workers;



@Repository
public interface WorkerRepo extends JpaRepository<Workers, Integer>{
Workers findById(int id);
List<Workers> findByCityAndRole(String city, String role);
Workers findByEmail(String email);
List<Workers> findAll();
List<Workers> findByCity(String city);
Workers findByPhone(String phone);



}
