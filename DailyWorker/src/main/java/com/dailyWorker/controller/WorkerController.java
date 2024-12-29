package com.dailyWorker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dailyWorker.entities.LoginCredential;
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

@GetMapping("/nearest-work")
public ResponseEntity<Object> searchNearestWork(@RequestParam int id){
	try {
		Workers worker = wsr.getById(id);
		String city = worker.getCity();
		List<Workes> workes = wsr.searchWork(city);
		String token = tokenHelper.genrateTokenforNearestWork(workes);
		return new ResponseEntity<>(token,HttpStatus.OK);
	} catch (Exception e) {
		log.error("Error Ocuured",e);
		return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
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

@GetMapping("/searchWork")
public ResponseEntity<Object> searchWork(@RequestParam String city){
	try {
		List<Workes> workes = wsr.searchWork(city);
		return new ResponseEntity<>(workes,HttpStatus.OK);
	} catch (NullPointerException e) {
		log.error("Errot occured",e);
		return new ResponseEntity<>("Work Not found",HttpStatus.OK);
	}
	catch (Exception e) {
		log.error("Error occured",e);
		return new ResponseEntity<>("Something went wrong",HttpStatus.OK);
	}
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
