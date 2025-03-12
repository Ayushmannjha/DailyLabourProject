package com.dailyWorker.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.dailyWorker.entities.Request;
import com.dailyWorker.entities.Transactions;
import com.dailyWorker.entities.Workers;
import com.dailyWorker.entities.Workes;
import com.dailyWorker.security.TokenHelper;
import com.dailyWorker.service.WorkerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/worker")
@Slf4j
public class WorkerController {

@Autowired	
private WorkerService wsr;	


@Autowired
private TokenHelper tokenHelper;



@GetMapping("/searchWork")
public ResponseEntity<Object> searchWork(@RequestParam String role, @RequestParam String city){
	try {
		List<Workes> result = new ArrayList<>();
		System.out.println(role+""+city);
		if(role!=null && city!=null) {
			List<Workes> workByRole = wsr.getWorkByLabourDetails(role);
			List<Workes> workByCity = wsr.getWorkByCity(city);
			List<Workes> search = wsr.getWorkByLbAndCity(role, city);
			result.addAll(workByRole);
			result.addAll(workByCity);
			result.addAll(search);
			String token = tokenHelper.genrateTokenForWorkSearch(result);
			return new ResponseEntity<>(token, HttpStatus.OK);
		}
		else if(role!=null&&city==null) {
			List<Workes> workByRole = wsr.getWorkByLabourDetails(role);
			result.addAll(workByRole);
			String token = tokenHelper.genrateTokenForWorkSearch(result);
			return new ResponseEntity<>(token, HttpStatus.OK);
		}
		else if(city!=null&&role==null) {
			List<Workes> workByCity = wsr.getWorkByCity(city);
			result.addAll(workByCity);
			String token = tokenHelper.genrateTokenForWorkSearch(result);
			return new ResponseEntity<>(token, HttpStatus.OK);
		}
		else {
				
			return new ResponseEntity<>("city and role both not be null", HttpStatus.BAD_REQUEST);
		}
		
	} catch (Exception e) {
		// TODO: handle exception

		return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_GATEWAY);
	}
}

@GetMapping("/get-notification-count")
public ResponseEntity<Object> getNotificationCount(@RequestParam int workerId){
	try {
		System.out.println(workerId);
		List<ReadNotification> notification = wsr.getByIsRead(workerId,0);
		List<Request>requests = wsr.getRequestByWorkerId(workerId);
		int count = 0;
		for(Request request:requests) {
			if(request.getStatus()==0) {
				count++;
			}
		}
		return new ResponseEntity<>(notification.size()+count,HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error occured", e);
		return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_REQUEST);
		
	}
}

@PostMapping("/request")
public ResponseEntity<Object> request(@RequestParam int position,@RequestParam int status, @RequestParam int userId, @RequestParam int workerId) {

	try {
		System.out.println("Position "+position+" Status "+status+" WorkerId "+workerId+" UserId "+userId);
		List<Request> requests = wsr.getRequestByUserIdAndWorkerId(userId, workerId);
		Request request = requests.get(position);
		request.setStatus(status);
		wsr.saveRequest(request);
		return new ResponseEntity<>("Request updated",HttpStatus.OK);
	} catch (Exception e) {
		log.error("Error occured",e);
		return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_GATEWAY);
	}
    
    
}

@PostMapping("/update-notification")
public ResponseEntity<Object> updateNotification(@RequestParam int workerId){
	try {
		System.out.println("Updation");
		List<ReadNotification> readNotificationDetails = wsr.getReadNotificationByWorkerId(workerId);
		for(ReadNotification read:readNotificationDetails) {
			if(read.getIsRead()==0) {
				read.setIsRead(1);
			}
			
			wsr.saveReadNotification(read);
		}
		return new ResponseEntity<>("Notification updated", HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Erro occured",e);
		return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_GATEWAY);
		
	}
}

@GetMapping("/get-notifications")
public ResponseEntity<Object> getWorkerNotifications(@RequestParam int workerId){
	try {
		
		if(workerId==-1) {
			return new ResponseEntity<>("Worker not fount",HttpStatus.BAD_REQUEST);
		}
		
		List<JobNotification> jobUpdates = wsr.getAllJobNotifications();
		List<JobNotification> jobUpdate = new ArrayList<>();
		Workers worker = wsr.getById(workerId);
		List<ReadNotification> readNotification = wsr.getReadNotificationByWorkerId(workerId);
		String city = worker.getCity();
		
		for(JobNotification job:jobUpdates) {
			if(job.getTitle().equalsIgnoreCase(city)) {
				jobUpdate.add(job);
			}
		}
	
		List<Request> request = wsr.getRequestByWorkerId(workerId);

		// Use an iterator to safely remove elements while iterating
		Iterator<Request> iterator = request.iterator();
		while (iterator.hasNext()) {
		    Request req = iterator.next();
		    if (req.getStatus() != 0) {
		        iterator.remove(); // Safe way to remove elements while iterating
		    }
		}

	List<Integer> userIds = new ArrayList<>();
	for(Request req:request) {
		
		int userId = req.getUserId();
		userIds.add(userId);
		
	}
	String token = tokenHelper.genrateTokenForNotification(jobUpdate, request,userIds,readNotification);
	return new ResponseEntity<>(token, HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error occured",e);
		e.printStackTrace();
		return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_GATEWAY);
		
	}
}


@GetMapping("/get-work-status")
public ResponseEntity<Object> getWorkStatus(@RequestParam int workId, @RequestParam int workerId){
	try {
		
		ApplicationStatus applicationStatus = wsr.getByWorkIdAndWorkerId(workerId, workId);
		List<ApplicationStatus> workerWorkes = wsr.getApplicationStatusByWorkerId(workerId);
		
		String token = tokenHelper.genrateTokenForApplicationStatus(workerWorkes,applicationStatus);
		return new ResponseEntity<>(token,HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error Occured",e);
		return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_REQUEST);
	}
}

@GetMapping("/get-highest-paying-jobs")
public ResponseEntity<Object> getHighestPayingJob(@RequestParam int workerId){
	try {
		List<Workes> workes = wsr.workesInDescendingOrder();
		Workers worker = wsr.getById(workerId);
		String role = worker.getRole();
		String[] roles = role.split(",");
		List<Workes> result = new ArrayList<>();
		
		
		
		for(int i = 0; i<workes.size(); i++) {
			Workes work = workes.get(i);
			String requirment = work.getLaboursDetails();
			
			for(int j = 0; j<roles.length; j++) {
				
				 Pattern pattern = Pattern.compile(roles[j]);
			        Matcher matcher = pattern.matcher(requirment);
				boolean check = matcher.find();
			if(check) {
				result.add(work);
				break;
			}
			}
			
		}
		
		String token = tokenHelper.genrateTokenForHighestPayingjobs(result);
		
		return new ResponseEntity<>(token,HttpStatus.OK);
		
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error Occured", e);
		e.printStackTrace();
		return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_GATEWAY);
	}
}

@GetMapping("/get-work-by-workerId")
public ResponseEntity<Object> getWork(@RequestParam int workerId){
	try {
		List<ApplicationStatus> applicationStatus = wsr.getApplicationStatusByWorkerId(workerId);
		List<Workes> workes = new ArrayList<>();
		for(ApplicationStatus app:applicationStatus) {
			Workes work = wsr.getWorkById(app.getWorkId());
			workes.add(work);
		}
		
		String token = tokenHelper.genrateTokenForApplicationStatusList(applicationStatus,workes);

		return new ResponseEntity<>(token, HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error Ocuured ",e);
		e.printStackTrace();
		return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_GATEWAY);
	}
}

@GetMapping("/nearest-work")
public ResponseEntity<Object> searchNearestWork(@RequestParam int id){
	try {
		Workers worker = wsr.getById(id);
		String city = worker.getCity();
		List<Workes> workes = wsr.searchWork(city);
		for(Workes works:workes) {
			System.out.println(works.getCity());
			System.out.println(works);
		}
		String token = tokenHelper.genrateTokenforNearestWork(workes);
		return new ResponseEntity<>(token,HttpStatus.OK);
	} catch (Exception e) {
		log.error("Error Ocuured",e);
		return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
	}
}

@PostMapping("/applyForJob")
public ResponseEntity<Object> appyForWork(@RequestParam int workerId,@RequestParam int workId){
	try {
		ApplicationStatus applicationStatus = new ApplicationStatus();
		applicationStatus.setWorkerId(workerId);
		applicationStatus.setWorkId(workId);
		applicationStatus.setStatus(0);
		wsr.saveApplicationStatus(applicationStatus);
		return new ResponseEntity<>(applicationStatus,HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		log.error("Error Occurred",e);
		return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_GATEWAY);
	}
}

@GetMapping("/profile")	
public String WorkerProfile(@RequestParam int id) {
try {
	Workers worker = wsr.getById(id);
	LoginCredential userDetails = wsr.getLoginCredentialByEmail(worker.getEmail());
	String token = tokenHelper.generateToken(userDetails, worker);
	return token;
} catch (Exception e) {
	log.error("Error occured",e);
	return "Something went wrong";
}	
}

@PutMapping("/profile-update")	
public String profileUpdate(@RequestBody Map<String,Object> changes, @RequestParam int id) {
try {
	Workers worker = wsr.getById(id);
	for(Map.Entry<String, Object> entry : changes.entrySet()) {
		if(entry.getKey()=="email"||entry.getKey()=="phone"){
			return "Email or phone cannot change";
		}
		if(entry.getKey()=="name") {
			worker.setName(entry.getValue().toString());
		}
		if(entry.getKey()=="workPlaces") {
			String workerPlaces = worker.getWorkPlaces();
			workerPlaces = addLocation(workerPlaces,entry.getValue().toString());
			worker.setWorkPlaces(workerPlaces);
			
		}
		if(entry.getKey()=="city") {
			worker.setCity(entry.getValue().toString());
		}
		if(entry.getKey()=="state") {
			worker.setState(entry.getValue().toString());
		}
		if(entry.getValue()=="avability") {
			worker.setAvability(Integer.parseInt(entry.getValue().toString()));
		}
		if(entry.getKey()=="locality") {
			worker.setLocality(entry.getValue().toString());
		}
		if(entry.getKey()=="pincode") {
			worker.setPincode(entry.getValue().toString());
		}
		if(entry.getKey()=="role"){
			worker.setRole(entry.getValue().toString());
		}
		if(entry.getKey()=="wallet") {
			worker.setWallet(Double.parseDouble(entry.getValue().toString()));	
		}
	}
	wsr.saveWorker(worker);
	return "Profile updated successfully";
} catch (Exception e) {
	log.error("Error occured",e);
	return "Something went wrong";
}	
}



public static String addLocation(String originalString, String newLocation) {
    // Remove the closing bracket
    String modifiedString = originalString.substring(0, originalString.length() - 1);

    // Trim any whitespace from new location
    newLocation = newLocation.trim();

    // Add the new location to the modified string
    modifiedString += ", " + newLocation + "]";

    return modifiedString;
}



@GetMapping("/wallet-history")
public ResponseEntity<Object> walletHistory(@RequestParam int id){
try {
	List<Transactions> transactions = wsr.getByWorkerId(id);
	return new ResponseEntity<>(transactions,HttpStatus.OK);
}catch (NullPointerException e) {
	log.error("Error Occured",e);
	return new ResponseEntity<Object>("Transaction not found",HttpStatus.OK);
}	
catch (Exception e) {
	log.error("Error Occured",e);
	return new ResponseEntity<Object>("Something went wrong",HttpStatus.OK);
}	
	
}

}
