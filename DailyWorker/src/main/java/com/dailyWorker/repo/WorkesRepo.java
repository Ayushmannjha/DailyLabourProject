package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.Workes;
import java.util.List;
import java.util.Optional;


@Repository
public interface WorkesRepo extends JpaRepository<Workes, Integer>{
List<Workes> findByCity(String city);

Workes save(Workes workes);

List<Workes> findAll();

Workes findById(int id);


}
