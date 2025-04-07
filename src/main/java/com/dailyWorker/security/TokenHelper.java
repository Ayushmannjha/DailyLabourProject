package com.dailyWorker.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.dailyWorker.entities.Admin;
import com.dailyWorker.entities.ApplicationStatus;
import com.dailyWorker.entities.JobNotification;
import com.dailyWorker.entities.LoginCredential;
import com.dailyWorker.entities.ReadNotification;
import com.dailyWorker.entities.Request;
import com.dailyWorker.entities.UserRequests;
import com.dailyWorker.entities.WebUser;
import com.dailyWorker.entities.Workers;
import com.dailyWorker.entities.Workes;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenHelper {
	
	public String genrateTokenForWorker(List<Workers> result, double budget){
		Map<String, Object> claims = new HashMap<>();
		claims.put("result", result);
		claims.put("budget", budget);
		return doGenerateToken(claims, secret);
	}

	public String genrateTokenForWorker(List<Workers> result, List<Double> budget){
		Map<String, Object> claims = new HashMap<>();
		claims.put("result", result);
		claims.put("budget", budget);
		return doGenerateToken(claims, secret);
	}

	
	public String genrateTokenForWorkSearch(List<Workes> result){
		Map<String, Object> claims = new HashMap<>();
		claims.put("result", result);
		return doGenerateToken(claims, secret);
	}
	
	
	public String genrateTokenForNotification(List<JobNotification> jobNotifications, List<Request> requestNotification, List<Integer> userIds,List<ReadNotification> readNotification) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("jobNotifications", jobNotifications);
		claims.put("requestNotification", requestNotification);
		claims.put("readNotification", readNotification);
		claims.put("userIds", userIds);
		return doGenerateToken(claims, secret);
	}

	public String genrateTokenforNearestWork(List<Workes> workes) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("workes", workes);
		return doGenerateToken(claims, secret);
	}
	
	public String genrateTokenForApplicationStatusList(List<ApplicationStatus> workerWorkes,List<Workes> workes,List<UserRequests> userRequest) {
		Map<String,Object> claims = new HashMap<>();
		claims.put("workes", workes);
		claims.put("workerWorkes", workerWorkes);
		claims.put("userRequests",userRequest);
		return doGenerateToken(claims, secret);
	}
	
	public String genrateTokenForHighestPayingjobs(List<Workes> workes) {
		Map<String,Object> claims = new HashMap<>();
		claims.put("workes", workes);
		
		return doGenerateToken(claims, secret);
	}
	public String genrateTokenForApplicationStatus(List<ApplicationStatus> workerWorkes,ApplicationStatus applicationStatus) {
		Map<String,Object> claims = new HashMap<>();
		claims.put("applicationStatus", applicationStatus);
		claims.put("workerWorkes", workerWorkes);
		return doGenerateToken(claims, secret);
	}
	
    public String generateToken(LoginCredential loginCredential, Workers worker) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", worker.getName());
        claims.put("email", worker.getEmail());
        claims.put("id", worker.getId());
        claims.put("phone", worker.getPhone());
        claims.put("role", worker.getRole());
        claims.put("availability", worker.getAvability());
        claims.put("state", worker.getState());
        claims.put("city", worker.getCity());
        claims.put("locality", worker.getLocality());
        claims.put("pincode", worker.getPincode());
        claims.put("work_places", worker.getWorkPlaces());
        claims.put("wallet", worker.getWallet());
        
        return doGenerateToken(claims, loginCredential.getEmail());
    }
    public String generateTokenForUpdate( Workers worker) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", worker.getName());
        claims.put("email", worker.getEmail());
        claims.put("id", worker.getId());
        claims.put("phone", worker.getPhone());
        claims.put("role", worker.getRole());
        claims.put("availability", worker.getAvability());
        claims.put("state", worker.getState());
        claims.put("city", worker.getCity());
        claims.put("locality", worker.getLocality());
        claims.put("pincode", worker.getPincode());
        claims.put("work_places", worker.getWorkPlaces());
        claims.put("wallet", worker.getWallet());
        
        return doGenerateToken(claims, worker.getEmail());
    }
    
public String genrateTokenForAdmin(LoginCredential loginCredential, Admin admin) {
	Map<String,Object> claims = new HashMap<>();
	claims.put("name", admin.getName());
	claims.put("email", admin.getEmail());
	claims.put("id", admin.getId());
	claims.put("email", admin.getEmail());
	
	return doGenerateToken(claims, loginCredential.getEmail());
}
    public String generateToken(LoginCredential loginCredential, WebUser webUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", webUser.getId());
        claims.put("email", webUser.getEmail());
        claims.put("phone", webUser.getPhone());
        claims.put("name", webUser.getName());
        claims.put("state", webUser.getState());
        claims.put("city", webUser.getCity());
        claims.put("locality", webUser.getLocality());
        claims.put("pincode", webUser.getPincode());
        claims.put("role", "USER");
        return doGenerateToken(claims, loginCredential.getEmail());
    }

    
    private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Issued time
                .signWith(SignatureAlgorithm.HS512, secret)  // Sign the token
                .compact();  // Do not set expiration
    }


    //validate token
    public Boolean validateToken(String token, LoginCredential userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getEmail()));  // No expiration check
   
}
}