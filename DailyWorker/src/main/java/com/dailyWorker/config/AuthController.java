package com.dailyWorker.config;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dailyWorker.dao.ErrorResponse;
import com.dailyWorker.dao.LoginRequest;
import com.dailyWorker.entities.Admin;
import com.dailyWorker.entities.LoginCredential;
import com.dailyWorker.entities.WebUser;
import com.dailyWorker.entities.Workers;
import com.dailyWorker.security.TokenHelper;
import com.dailyWorker.service.WorkerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private WorkerService workerService;

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
//----------------Registration--------------------------//
    		
    //----Worker---//
    @PostMapping("/register-worker")
    public ResponseEntity<Object> registerWorker(@RequestBody Workers worker) {
        try {
            // Step 1: Set default values and encode password
            worker.setAvability(0);
            worker.setPassword(passwordEncoder.encode(worker.getPassword()));

            // Step 2: Save the worker
            Workers savedWorker = workerService.saveWorker(worker);

            // Step 3: Create LoginCredential object
            LoginCredential loginCredential = new LoginCredential();
            loginCredential.setEmail(savedWorker.getEmail());
            loginCredential.setPhone(savedWorker.getPhone());
            loginCredential.setPassword(savedWorker.getPassword());
            loginCredential.setRole("WORKER");

            workerService.saveLoginCredential(loginCredential);

            // Step 5: Generate the JWT token using LoginCredential
            String token = tokenHelper.generateToken(loginCredential, worker);
            
            if (token == null) {
                log.error("Token generation failed for worker: {}", savedWorker.getEmail());
                return new ResponseEntity<>("Token not generated", HttpStatus.OK);
            }

            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            log.error("Worker already exists: {}", worker.getEmail(), e);
            return new ResponseEntity<>("Worker already exists", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during registration for worker: {}", worker.getEmail(), e);
            return new ResponseEntity<>("Registration failed", HttpStatus.OK);
        }
    }

    
    
    
    //-----User-----//
    @PostMapping("/register-user")
    public ResponseEntity<Object> registerUser(@RequestBody WebUser webUser) {
        try {
            // Step 1: Encode password and set it on the user
            webUser.setPassword(passwordEncoder.encode(webUser.getPassword()));

            // Step 2: Save the user
            WebUser savedWebUser = workerService.saveWebUser(webUser);

            // Step 3: Create LoginCredential object
            LoginCredential loginCredential = new LoginCredential();
            
            loginCredential.setEmail(savedWebUser.getEmail());
            loginCredential.setPhone(savedWebUser.getPhone());
            loginCredential.setPassword(savedWebUser.getPassword());
            loginCredential.setRole("USER");
            // Step 4: Authenticate the user
            workerService.saveLoginCredential(loginCredential);
            

            // Step 5: Generate the JWT token using LoginCredential
            String token = tokenHelper.generateToken(loginCredential, savedWebUser);

            if (token == null) {
                log.error("Token generation failed for user: {}", savedWebUser.getEmail());
                return new ResponseEntity<>("Token not generated", HttpStatus.OK);
            }

            // Return the generated token
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            log.error("User already exists: {}", webUser.getEmail(), e);
            return new ResponseEntity<>("Token not generated", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during registration for user: {}", webUser.getEmail(), e);
            return new ResponseEntity<>("Token not generated", HttpStatus.OK);
        }
    }

    
    @PostMapping("/register-admin")
    public ResponseEntity<Object> registerAdmin(@RequestBody Admin admin){
    	try {
    		workerService.saveAdmin(admin);
    		LoginCredential cr = new LoginCredential();
    		cr.setEmail(admin.getEmail());
    		cr.setPassword(admin.getPassword());
    		cr.setRole("ADMIN");
    		cr.setPhone(admin.getPhone());
    		workerService.saveLoginCredential(cr);
    		return new ResponseEntity<>("Admin register Successfully",HttpStatus.OK);
    	} catch (DataIntegrityViolationException e) {
    		return new ResponseEntity<>("User Already exist", HttpStatus.CONFLICT);
    	}
    	catch (Exception e) {
    		return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_GATEWAY);
    	}
    }
    
    @PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
    	System.err.println(request.getEmail()+" "+request.getPassword());
		this.doAuthenticate(request.getEmail(), request.getPassword());

		LoginCredential userDetails = workerService.getLoginCredentialByEmail(request.getEmail());
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		// Iterate through the authorities and print the roles
		boolean isUser = false;
		boolean isWorker = false;
		boolean isAdmin = false;
		for (GrantedAuthority authority : authorities) {
			String role = authority.getAuthority();
			// logic of checking role//
			if ("ROLE_USER".equals(role)) {
				isUser = true;
			} else if ("ROLE_WORKER".equals(role)) {
				isWorker = true;
			}else if("ROLE_ADMIN".equals(role)) {
				isAdmin = true;
			}
		}

		String token = "";
		 if (isUser) {
			WebUser user = workerService.loginWebUser(request.getEmail() );
			
			token =tokenHelper.generateToken(userDetails, user);
			return new ResponseEntity<>(token, HttpStatus.OK);
		} else if (isWorker) {
			
			Workers worker = workerService.loginWorker(request.getEmail());
			
			token = tokenHelper.generateToken(userDetails, worker);
			return new ResponseEntity<>(token, HttpStatus.OK);
		} else if(isAdmin) {
			Admin admin = workerService.getAdminByEmail(token);
			token = tokenHelper.genrateTokenForAdmin(userDetails,admin);
			return new ResponseEntity<>(token,HttpStatus.OK);
		}
		else {
			 return new ResponseEntity<>("Login failed", HttpStatus.BAD_REQUEST);
        }

	}
    
    private void doAuthenticate(String email, String password) {
        LoginCredential userDetails = workerService.getLoginCredentialByEmail(email);
       
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid Username or Password");
        }

        // Check if the raw password matches the encoded password
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Username or Password");
        }
    }


	@ExceptionHandler(BadCredentialsException.class)
	public ErrorResponse exceptionHandler() {
		ErrorResponse er = new ErrorResponse();
		er.setSuccess(false);
		er.setMessage("Invalid Username or password");
		return er;
	}
}
