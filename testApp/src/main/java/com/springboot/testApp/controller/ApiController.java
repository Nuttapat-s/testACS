package com.springboot.testApp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.testApp.model.JsonStatus;
import com.springboot.testApp.model.LoginForm;
import com.springboot.testApp.model.User;
import com.springboot.testApp.service.UserService;

@RestController
public class ApiController {
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/register",method =RequestMethod.POST)
	@ResponseBody
		public ResponseEntity<JsonStatus> addRegisterUser(@RequestBody User user) {
			return userService.addUser(user);
		}
	
	@RequestMapping(value = "/login",method =RequestMethod.GET)
	@ResponseBody
		public ResponseEntity<JsonStatus> getUser(@RequestBody LoginForm loginForm) {
			return userService.getLoginUser(loginForm);
		}
	
	@GetMapping(value = "/member-type")
	@ResponseBody
		public ResponseEntity<JsonStatus> getMemberType(HttpServletRequest request) {
			return userService.getMemberType(request.getHeader("Authorization"));
		}
	
	
}
