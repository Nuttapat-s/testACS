package com.springboot.testApp.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.springboot.testApp.model.Instance;
import com.springboot.testApp.model.JsonStatus;
import com.springboot.testApp.model.LoginForm;
import com.springboot.testApp.model.User;
import com.springboot.testApp.model.resultDataUser;
import com.springboot.testApp.repository.UserReposity;
import com.springboot.testApp.service.utils.JwtUtils;

@Service
public class UserService {

	@Autowired
	private UserReposity userReposity;

	@Autowired
	MyUserDetailService userDetilService;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private AuthenticationManager authenticationManager;

	public ResponseEntity<JsonStatus> getLoginUser(LoginForm loginForm) {
		JsonStatus jsonStatus = new JsonStatus();
		resultDataUser resultData = new resultDataUser();
		Instance instance = new Instance();
		UserDetails userDetails;
		try {
			userDetails = userDetilService.loadUserByUsername(loginForm.getUsername(),instance);
			boolean check = checkPassword(loginForm.getPassword(), userDetails.getPassword());

			if (!check) {
				jsonStatus.setResultCode("401");
				jsonStatus.setResultDesc("Wrong pass word");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonStatus);
			}
		} catch (Exception e) {
			jsonStatus.setResultCode("404");
			jsonStatus.setResultDesc("User not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonStatus);
		}

//		List<User> result =  userReposity.findByUsername(loginForm.getUsername());

		String token = genToken(userDetails);
		if (token.equals("error")) {
			jsonStatus.setResultCode("401");
			jsonStatus.setResultDesc("Gen Token ERROR");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonStatus);
		}

		jsonStatus.setResultCode("200");
		jsonStatus.setResultDesc("SUCCESS");
		resultData.setUserData(instance.getUserData());
		resultData.setToken(token);
		jsonStatus.setResultData(resultData);
		return ResponseEntity.ok().body(jsonStatus);
	}

	public ResponseEntity<JsonStatus> addUser(User user) {
		JsonStatus jsonStatus = new JsonStatus();
		try {

			if (!checkLoginObj(user)) {
				jsonStatus.setResultCode("400");
				jsonStatus.setResultDesc("USER IS NULL");
				return ResponseEntity.badRequest().body(jsonStatus);
			} else {
				user = genReferenceCode(user);
				userReposity.save(user);
			}

		} catch (IllegalArgumentException e) {
			jsonStatus.setResultCode("502");
			jsonStatus.setResultDesc("DB error");
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(jsonStatus);
		}
		jsonStatus.setResultCode("200");
		jsonStatus.setResultDesc("SUCCESS");

		return ResponseEntity.ok(jsonStatus);

	}

	public ResponseEntity<JsonStatus> getMemberType(String token) {
		if(token.contains("Bearer ")) {
			token = token.replace("Bearer ","");
		}
		JsonStatus jsonStatus = new JsonStatus();
		String username = jwtUtils.validateToken(token);
		if (username != null) {
			List<User> result = userReposity.findByUsername(username);
			if (result == null) {
				jsonStatus.setResultCode("404");
				jsonStatus.setResultDesc("User not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonStatus);
			} else {
				String type = validateMemberType(result.get(0).getSalary());
				if(type.equals("")) {
					jsonStatus.setResultCode("400");
					jsonStatus.setResultDesc("Rejects");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonStatus);
				}else {
					jsonStatus.setResultCode("200");
					jsonStatus.setResultDesc("SUCCESS");
					jsonStatus.setResultData(type);
					return ResponseEntity.ok(jsonStatus);
				}
			}
		}
		
		jsonStatus.setResultCode("401");
		jsonStatus.setResultDesc("Token erro OR Exprie");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jsonStatus);

	}

	private boolean checkPassword(String password, String dbPassword) {
		if (password.equals(dbPassword)) {
			return true;
		}
		return false;
	}

	private boolean checkLoginObj(User user) {
		if (user == null) {
			return false;
		} else {
			if ((user.getUsername() == null || user.getUsername().equals(""))
					|| (user.getPassword() == null || user.getPassword().equals(""))
					|| (user.getPhoneNumber() == null || user.getPhoneNumber().equals(""))) {
				return false;
			}
		}
		return true;
	}

	private User genReferenceCode(User user) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();
		String strNow = sdfDate.format(now);

		String subStrPhoneNo = user.getPhoneNumber().substring(6);

		String referCode = strNow + subStrPhoneNo;

		user.setReferenceCode(referCode);

		return user;
	}

	private String genToken(UserDetails userDetails) {
		String token = "";
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword()));
		} catch (Exception e) {
			token = "error";
		}

		token = jwtUtils.generateToken(userDetails);

		return token;
	}

	private String validateMemberType(int salary) {
		String result = "";
		if (salary > 50000) {
			result = "Platinum";
		}else if(salary > 30000 && salary < 50000){
			result = "Gold";
		}else if(salary < 30000 && salary > 15000) {
			result = "Silver";
		}
		
		return result;
	}

}
