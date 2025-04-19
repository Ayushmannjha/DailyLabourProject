package com.dailyWorker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dailyWorker.entities.Admin;
import com.dailyWorker.entities.ApplicationStatus;
import com.dailyWorker.entities.JobNotification;
import com.dailyWorker.entities.LoginCredential;
import com.dailyWorker.entities.ReadNotification;
import com.dailyWorker.entities.ReadNotificationUserReq;
import com.dailyWorker.entities.Request;
import com.dailyWorker.entities.Transactions;
import com.dailyWorker.entities.UserRequests;
import com.dailyWorker.entities.Verification;
import com.dailyWorker.entities.WebUser;
import com.dailyWorker.entities.Workers;
import com.dailyWorker.entities.Workes;
import com.dailyWorker.repo.AdminRepo;
import com.dailyWorker.repo.ApplicationStatusRepo;
import com.dailyWorker.repo.JobNotificationRepo;
import com.dailyWorker.repo.LoginCredendialRepo;
import com.dailyWorker.repo.ReadNotificationRepo;
import com.dailyWorker.repo.ReadNotificationUserReqRepo;
import com.dailyWorker.repo.RequestRepo;
import com.dailyWorker.repo.TransactionRepo;
import com.dailyWorker.repo.UserRequestsRepo;
import com.dailyWorker.repo.VerificationRepo;
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

@Autowired
private JobNotificationRepo jobRepo;

@Autowired
private ApplicationStatusRepo applicationRepo;

@Autowired
private RequestRepo reqRepo;

@Autowired
private ReadNotificationRepo readRepo;

@Autowired
private VerificationRepo vrepo;

@Autowired
private UserRequestsRepo ureqRepo;

@Autowired
private ReadNotificationUserReqRepo readNotiUsrReq;

//--------Application status--------//

public ApplicationStatus saveApplicationStatus(ApplicationStatus applicationStatus) {
	return applicationRepo.save(applicationStatus);
}

public ApplicationStatus getApplicationStatusById(int id) {
	return applicationRepo.findById(id);
}

public List<ApplicationStatus> getApplicationStatusByWorkId(int id){
	return applicationRepo.findByWorkId(id);
}

public List<ApplicationStatus> getApplicationStatusByWorkerId(int id){
	return applicationRepo.findByWorkerId(id);
}
public ApplicationStatus getByWorkerIdAndWorkId( int workerId,int workId ) {
	return applicationRepo.findByWorkerIdAndWorkId(workerId, workId);
}

//----------------Worker services----------------//
public List<Workes> workesInDescendingOrder(){
	return wrepo.findAllByOrderByBudgetDesc();
}

public Workers saveWorker(Workers worker) {
	return repo.save(worker);
}

public Workers loginWorker(String email) {
	return repo.findByEmail(email);
}

public Workers getById(int id) {
	return repo.findById(id);
}
public Workes getWorkById(int id) {
	return wrepo.findById(id);
}

public Workers getWorkerByEmail(String email){
	return repo.findByEmail(email);
}

public Workers getWorkerByPhone(String phone) {
	return repo.findByPhone(phone);
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

//-----------Notification settings---------------//

public ReadNotificationUserReq saveReadNotificationUserReq(ReadNotificationUserReq req){
	return readNotiUsrReq.save(req);
}

public List<ReadNotificationUserReq> getReadNotificationUserReqByWorkerId(int workerId){
	return readNotiUsrReq.findByWorkerId(workerId);
}

public List<JobNotification> getAllJobNotifications(){
	return jobRepo.findAll();
}

public List<Request> getRequestByUserIdAndWorkerId(int userId, int workerId){
	return reqRepo.findByUserIdAndAndWorkerId(userId, workerId);
}

public List<Request> getRequestByWorkerId(int workerId){
	return reqRepo.findByWorkerId(workerId);
}

public JobNotification saveJobNotification(JobNotification job) {
	return jobRepo.save(job);
}

public Request saveRequest(Request request) {
	return reqRepo.save(request);
}

public List<ReadNotification> getByIsRead(int workerId,int isRead) {
	return readRepo.findByWorkerIdAndIsRead(workerId,isRead);
}

public List<ReadNotification> getReadNotificationByWorkerId(int workerId){
	return readRepo.findByWorkerId(workerId);
}
public ReadNotification saveReadNotification(ReadNotification read){
	return readRepo.save(read);
}
//----------search for work------------------//
public List<Workes> getWorkByLabourDetails(String details){
	return wrepo.findByLaboursDetailsContaining(details);
}
public List<Workes> getWorkByCity(String city){
	return wrepo.findByCity(city);
}
public List<Workes> getWorkByLbAndCity(String details, String city){
	return wrepo.findByLaboursDetailsContainingAndAndCity(details, city);
}

//------transaction------//
public Transactions saveTransaction(Transactions transaction){
	return trRepo.save(transaction);
}
public Optional<Transactions> getTxById(String tid){
	return trRepo.findByTxId(tid);
}
//verification----------
public Verification saveVerification(Verification v) {
	return vrepo.save(v);
}
public Verification getVByWorkerId(int workerId) {
	return vrepo.findByWorkerId(workerId);
}
//works
public List<Workes> getWorkByUserId(int userId){
	return wrepo.findByUserId(userId);
}
public List<Integer> getWorkerIdsStatusByWorkerId(int workId,int status){
	return applicationRepo.findWorkerIdsByWorkId(workId,status);
}
//userRequest
public List<UserRequests> getUserRequestByUserId(int userId){
	return ureqRepo.findByUserId(userId);
}

public List<UserRequests> getUserRequestByWorkerId(int userId){
	return ureqRepo.findByWorkerId(userId);
}

public UserRequests saveUserRequest(UserRequests userReq){
	return ureqRepo.save(userReq);
}
public UserRequests getUserRequestById(int id){
	return ureqRepo.findById(id);
}
public UserRequests getUserReqByWorkIdAndWorkerId(int workId,int workerId) {
	return ureqRepo.findByWorkIdAndWorkerId(workId, workerId);
}

}
