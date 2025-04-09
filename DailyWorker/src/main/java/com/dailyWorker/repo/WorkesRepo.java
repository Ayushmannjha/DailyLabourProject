package com.dailyWorker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailyWorker.entities.Workes;
import java.util.List;
import java.util.Optional;


@Repository
public interface WorkesRepo extends JpaRepository<Workes, Integer>{
List<Workes> findByCity(String city);

List<Workes> findByLaboursDetailsContaining(String labourDetail);

List<Workes> findByLaboursDetailsContainingAndAndCity(String laboursDetails, String city);

Workes save(Workes workes);

List<Workes> findByUserId(int userId);

List<Workes> findAll();

Workes findById(int id);

List<Workes> findAllByOrderByBudgetDesc();



}
