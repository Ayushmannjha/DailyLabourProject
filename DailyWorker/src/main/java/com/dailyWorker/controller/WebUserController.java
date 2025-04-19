package com.dailyWorker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dailyWorker.entities.ApplicationStatus;
import com.dailyWorker.entities.JobNotification;
import com.dailyWorker.entities.LoginCredential;
import com.dailyWorker.entities.ReadNotification;
import com.dailyWorker.entities.ReadNotificationUserReq;
import com.dailyWorker.entities.UserRequests;
import com.dailyWorker.entities.WebUser;
import com.dailyWorker.entities.Workers;
import com.dailyWorker.entities.Workes;
import com.dailyWorker.security.TokenHelper;
import com.dailyWorker.service.WorkerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
public class WebUserController {

@Autowired	
private WorkerService wsr;	


@Autowired
private TokenHelper tokenHelper;



@GetMapping("/profile")	
public ResponseEntity<Object> WorkerProfile(@RequestParam int id) {
try {
	WebUser webUser = wsr.getByUserId(id);
	LoginCredential userDetails = wsr.getLoginCredentialByEmail(webUser.getEmail());
	String token = tokenHelper.generateToken(userDetails, webUser);
	return new ResponseEntity<>(token,HttpStatus.OK);
} catch (Exception e) {
	log.error("Error occured",e);
	return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_REQUEST);
}	
}

@PostMapping("/search-worker")
public ResponseEntity<Object> searchForWorker(@RequestBody Map<String , Object> request){
try {
	String city = "";
	String role = "";
	if(request.size()==2) {
		for(Map.Entry<String, Object> entry: request.entrySet() ) {
			if(entry.getKey()=="city") {
				city = entry.getValue().toString();
			}
			if(entry.getKey()=="role") {
				role = entry.getValue().toString();
			}
		}
		List<Workers> workers = wsr.searchByCityAndRole(city, role);
		return new ResponseEntity<>(workers,HttpStatus.OK);
	}
	else {
		return new ResponseEntity<>("City and role must not be null",HttpStatus.OK);
	}
} catch (Exception e) {
	log.error("Error Occurred",e);
	return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
}
	
}
@GetMapping("/works")
public ResponseEntity<Object> works(@RequestParam int userId){
	try {
		List<Workes> workes = wsr.getWorkByUserId(userId);
		
		String token = tokenHelper.genrateTokenforNearestWork(workes);
		return new ResponseEntity<>(token, HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error occured ",e);
		return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_GATEWAY);
	}
}
@GetMapping("/requests")
public ResponseEntity<Object> requests(@RequestParam int workId){
	try {
		List<Integer> workIds = wsr.getWorkerIdsStatusByWorkerId(workId,0);
		Workes work = wsr.getWorkById(workId);
		System.out.println(workId);
		System.out.println(work.getBudget());
		List<Workers> workers  = new ArrayList<>();
		for(int id:workIds){
			Workers worker = wsr.getById(id);
			
			workers.add(worker);
		}
		String token = tokenHelper.genrateTokenForWorker(workers,work.getBudget());
		return new ResponseEntity<>(token, HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error occured",e);
		e.printStackTrace();
		return new ResponseEntity<>("Something went wrong ",HttpStatus.BAD_GATEWAY);
	}
}
@GetMapping("/accepted-requests")
public ResponseEntity<Object> acceptedRequests(@RequestParam int workId){
	try {
		System.out.println(workId);
		List<Integer> workIds = wsr.getWorkerIdsStatusByWorkerId(workId,1);
		Workes work = wsr.getWorkById(workId);
		System.out.println(workId);
		List<Double> workerBudgets = new ArrayList<>();
		System.out.println(work.getBudget());
		List<Workers> workers  = new ArrayList<>();
		for(int id:workIds){
			Workers worker = wsr.getById(id);
			System.out.println("Work Id"+workId+" worker ID"+id);
			
			UserRequests re = wsr.getUserReqByWorkIdAndWorkerId(workId, worker.getId());
			if(re!=null) {
				workerBudgets.add(re.getBudget());
			}
			workers.add(worker);
			
		}
		System.out.println(workers);
		System.out.println(workerBudgets);
		String token = tokenHelper.genrateTokenForWorker(workers,workerBudgets);
		return new ResponseEntity<>(token, HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error occured",e);
		e.printStackTrace();
		return new ResponseEntity<>("Something went wrong ",HttpStatus.BAD_GATEWAY);
	}
}
@GetMapping("/check-request-status")
public ResponseEntity<Object> checkRequestStatus(@RequestParam int workId,@RequestParam int workerId){
	try {
		UserRequests req = wsr.getUserReqByWorkIdAndWorkerId(workId, workerId);
		;
		System.out.println(req.getStatus());
		return new ResponseEntity<Object>(req,HttpStatus.OK);
	} catch (Exception e) {
		log.error("Error occured",e);
		e.printStackTrace();
		return new ResponseEntity<>("Something went wrong ",HttpStatus.BAD_GATEWAY);
	}
}
@PostMapping("/change-budget")
public ResponseEntity<Object> changeBudget(@RequestParam int workId,@RequestParam int workerId, @RequestParam double budget ){
	try {
		UserRequests req = wsr.getUserReqByWorkIdAndWorkerId(workId, workerId);
		req.setBudget(budget);
		req.setStatus(0);
		wsr.saveUserRequest(req);
		return new ResponseEntity<>("Budget updated", HttpStatus.OK);
	} catch (Exception e) {
		log.error("Error occured",e);
		e.printStackTrace();
		return new ResponseEntity<>("Something went wrong ",HttpStatus.BAD_GATEWAY);
	}
}

@PostMapping("/send-request")
public ResponseEntity<Object> sendRequest(@RequestBody UserRequests req){
	try {
		UserRequests request = wsr.saveUserRequest(req);
		System.out.println(req);
		ApplicationStatus apS = wsr.getByWorkerIdAndWorkId( request.getWorkerId(),request.getWorkId());
		ReadNotification notiReq = new ReadNotification();
		notiReq.setIsRead(0);
		notiReq.setCategory("jobRequest");
		notiReq.setWorkerId(request.getWorkerId());
		wsr.saveReadNotification(notiReq);
		wsr.saveApplicationStatus(apS);
		return new ResponseEntity<>(request,HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error occured ",e);
		e.printStackTrace();
		
		return new ResponseEntity<>("Something went",HttpStatus.BAD_GATEWAY);
	}
}

@GetMapping("/update-requests")
public ResponseEntity<Object> updateRequest(@RequestParam int workId, @RequestParam int workerId){
	try {
		UserRequests request = wsr.getUserReqByWorkIdAndWorkerId(workId, workerId);
		if(request!=null) {
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(false, HttpStatus.OK);
		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
		return new ResponseEntity<>(false, HttpStatus.BAD_GATEWAY);
	}
}

@PostMapping("/decline")
public ResponseEntity<Object> decline(@RequestParam int workId, @RequestParam int workerId){
	try {
		ApplicationStatus ap = wsr.getByWorkerIdAndWorkId(workerId, workId);
		ap.setStatus(3);
		wsr.saveApplicationStatus(ap);
		return new ResponseEntity<>("Declined ",HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error occured ",e);
		e.printStackTrace();
		
		return new ResponseEntity<>("Something went",HttpStatus.BAD_GATEWAY);
	}
}

@PutMapping("/profile-update")	
public ResponseEntity<Object>  profileUpdate(@RequestBody Map<String,Object> changes, @RequestParam int id) {
try {
	WebUser worker = wsr.getByUserId(id);
	for(Map.Entry<String, Object> entry : changes.entrySet()) {
		if(entry.getKey()=="email"||entry.getKey()=="phone"){
			return new ResponseEntity<>("Email or phone cannot change",HttpStatus.BAD_REQUEST);
		}
		if(entry.getKey()=="name") {
			worker.setName(entry.getValue().toString());
		}
		
		if(entry.getKey()=="city") {
			worker.setCity(entry.getValue().toString());
		}
		if(entry.getKey()=="state") {
			worker.setState(entry.getValue().toString());
		}
		
		if(entry.getKey()=="locality") {
			worker.setLocality(entry.getValue().toString());
		}
		if(entry.getKey()=="pincode") {
			worker.setPincode(entry.getValue().toString());
		}
		
	}
	wsr.saveWebUser(worker);
	return new ResponseEntity<>("Profile updated successfully",HttpStatus.OK);
} catch (Exception e) {
	log.error("Error occured",e);
	return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_GATEWAY);
}	
}



@PostMapping("/upload-work")
public ResponseEntity<Object> uploadWork(@RequestBody Map<String, Object> request,@RequestParam int id) {
try {
	System.out.println(request);
	WebUser wbUser = wsr.getByUserId(id);
	
	Workes work = new Workes();
	work.setUserId(id);
	work.setOwnerName(wbUser.getName());
	work.setBudget(Double.parseDouble(request.get("budget").toString()));
	work.setCity(request.get("city").toString());
	work.setState(request.get("state").toString());
	work.setLaboursDetails(request.get("labourDetails").toString());
	work.setPincode(request.get("pincode").toString());
	work.setPhone(wbUser.getPhone());
	work.setDescription(request.get("description").toString());
	
	
	wsr.saveWorkes(work);
	
	
	JobNotification newJob = new JobNotification();
	newJob.setTitle(work.getCity());
	newJob.setMsg("New job update");
	wsr.saveJobNotification(newJob);
	List<Workers> workers = wsr.getWorkerByCity(work.getCity());
	for(Workers worker:workers) {
		ReadNotification readNotification = new ReadNotification();
		readNotification.setIsRead(0);
		readNotification.setCategory("jobNotification");
		readNotification.setJobNotificationId(newJob.getId());
		readNotification.setWorkerId(worker.getId());
		wsr.saveReadNotification(readNotification);
	}
	
	return new ResponseEntity<>("Workes uploaded",HttpStatus.OK);
} catch (Exception e) {
	log.error("Error occured",e);
	return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_GATEWAY);
}	
}

public ResponseEntity<Object> nearestWork(@RequestParam int id){
	try {
		Workers worker = wsr.getById(id);
		List<Workes> workes = wsr.searchWork(worker.getCity());
		return new ResponseEntity<>(workes,HttpStatus.OK);
	} catch (Exception e) {
		log.error("Error occured",e);
		return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_REQUEST);
	}
}


}
