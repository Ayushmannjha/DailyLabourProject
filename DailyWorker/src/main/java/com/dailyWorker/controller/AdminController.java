package com.dailyWorker.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dailyWorker.entities.Workers;
import com.dailyWorker.entities.Workes;
import com.dailyWorker.service.WorkerService;

@RestController
@RequestMapping("/admin")
public class AdminController {
@Autowired	
private WorkerService sr;
@GetMapping("/workes")
public ResponseEntity<Object> workes(){
	try {
		List<Workes> workes = sr.getAllWorkes();
		return new ResponseEntity<>(workes,HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_GATEWAY);
	}
	
}
@GetMapping("/workers")
public ResponseEntity<Object> workers(){
	try {
		List<Workers> workes = sr.getAllWorkers();
		return new ResponseEntity<>(workes,HttpStatus.OK);
	} catch (Exception e) {
		// TODO: handle exception
		return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_GATEWAY);
	}
	
}

@GetMapping("/find-worker-by-city")
public ResponseEntity<Object> findWorkerByCity(@RequestParam String city){
	try {
		List<Workers> workers = sr.getAllWorkers();
		List<Workers> result = new ArrayList<>();
		for(Workers wr:workers) {
			if(containCity(wr.getWorkPlaces(),city)) {
				result.add(wr);
			}
		}
		return new ResponseEntity<>(result,HttpStatus.OK);
		
	} catch (Exception e) {
		// TODO: handle exception
		return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_GATEWAY);
	}
}

@GetMapping("/find-worker-by-city-role")
public ResponseEntity<Object> findWorkerByCityAndRole(@RequestParam String city, @RequestParam String role){
	try {
		List<Workers> workers = sr.getAllWorkers();
		List<Workers> result = new ArrayList<>();
		for(Workers wr:workers) {
			if(containCity(wr.getWorkPlaces(),city)&&containCity(wr.getRole(), role)) {
				result.add(wr);
			}
		}
		return new ResponseEntity<>(result,HttpStatus.OK);
		
	} catch (Exception e) {
		// TODO: handle exception
		return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_GATEWAY);
	}
}



//-----------utility function-----------//
public boolean containCity(String str, String city) {
	str = str.substring(1,str.length()-1);
	String[] citiesArray = str.split(",");
	for(String cityInList:citiesArray) {
		if(cityInList.trim().equalsIgnoreCase(city)) {
			return true;
		}
	}
	return false;
}

}
