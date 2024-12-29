package com.dailyWorker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dailyWorker.entities.Admin;
import com.dailyWorker.entities.LoginCredential;
import com.dailyWorker.entities.Transactions;
import com.dailyWorker.entities.WebUser;
import com.dailyWorker.entities.Workers;
import com.dailyWorker.entities.Workes;
import com.dailyWorker.repo.AdminRepo;
import com.dailyWorker.repo.LoginCredendialRepo;
import com.dailyWorker.repo.TransactionRepo;
import com.dailyWorker.repo.WebUserRepo;
import com.dailyWorker.repo.WorkerRepo;
import com.dailyWorker.repo.WorkesRepo;

@Service
public class WorkerService {
	
@Autowired	
private WorkerRepo repo;

@Autowired
private WebUserRepo userRepo;

@Autowired
private WorkesRepo wrepo;

@Autowired
private TransactionRepo trRepo;

@Autowired
private LoginCredendialRepo logRepo;

@Autowired
private AdminRepo adminRepo;

//----------------Worker services----------------//


public Workers saveWorker(Workers worker) {
	return repo.save(worker);
}

public Workers loginWorker(String email) {
	return repo.findByEmail(email);
}

public Workers getById(int id) {
	return repo.findById(id);
}


//--------------------------------------------------

//------------webuser----------------------------
public WebUser saveWebUser(WebUser webUser) {
	return userRepo.save(webUser);
}
public Workes saveWorkes(Workes work){
	return wrepo.save(work);
}


public WebUser loginWebUser(String email){
	return userRepo.findByEmail(email);
}


public WebUser getByUserId(int id) {
	return userRepo.findById(id);
}

public List<Transactions> getByWorkerId(int id){
	return trRepo.findByWorkerId(id);
}

public List<Workes> searchWork(String city){
	return wrepo.findByCity(city );
}
public List<Workers> searchByCityAndRole(String city, String role){
	return repo.findByCityAndRole(city, role);
}

public LoginCredential saveLoginCredential(LoginCredential log) {
	return logRepo.save(log);
}

public LoginCredential getLoginCredentialById(int id) {
	return logRepo.findById(id);
}

public LoginCredential getLoginCredentialByEmail(String email) {
	return logRepo.findByEmail(email);
}

public LoginCredential getLoginCredentialByPhone(String  phone) {
	return logRepo.findByPhone(phone);
}
//--------------------Admin------------------//
public Admin saveAdmin(Admin admin) {
	return adminRepo.save(admin);
}

public List<Workes> getAllWorkes(){
	return wrepo.findAll();
}
public List<Workers> getAllWorkers(){
	return repo.findAll();
}
public List<Workers> getWorkerByCity(String city){
	return repo.findByCity(city);
}
public Admin getAdminByEmail(String email) {
	return adminRepo.findByEmail(email);
}
}
