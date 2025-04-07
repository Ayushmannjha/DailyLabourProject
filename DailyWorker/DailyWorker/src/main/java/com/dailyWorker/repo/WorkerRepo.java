package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.Workers;
import java.util.List;



@Repository
public interface WorkerRepo extends JpaRepository<Workers, Integer>{
Workers findById(int id);
List<Workers> findByCityAndRole(String city, String role);
Workers findByEmail(String email);
List<Workers> findAll();
List<Workers> findByCity(String city);
Workers findByPhone(String phone);
}
