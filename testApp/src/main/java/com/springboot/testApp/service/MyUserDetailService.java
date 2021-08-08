package com.springboot.testApp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springboot.testApp.model.Instance;
import com.springboot.testApp.repository.UserReposity;

@Service
public class MyUserDetailService implements UserDetailsService{

	@Autowired
	private UserReposity userReposity;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Instance instance = new Instance();
		List<com.springboot.testApp.model.User> result =  userReposity.findByUsername(username);
		if(result == null || result.isEmpty()) {
			throw new UsernameNotFoundException(username);
		}
		instance.setUserData(result.get(0));
		return new User(result.get(0).getUsername(), result.get(0).getPassword(), new ArrayList<>());
	}
	
	public UserDetails loadUserByUsername(String username,Instance instance) throws UsernameNotFoundException {
		List<com.springboot.testApp.model.User> result =  userReposity.findByUsername(username);
		if(result == null || result.isEmpty()) {
			throw new UsernameNotFoundException(username);
		}
		instance.setUserData(result.get(0));
		return new User(result.get(0).getUsername(), result.get(0).getPassword(), new ArrayList<>());
	}
	

}
