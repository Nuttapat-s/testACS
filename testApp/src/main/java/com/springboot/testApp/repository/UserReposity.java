package com.springboot.testApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.testApp.model.User;

@Repository
public interface UserReposity extends JpaRepository<User, Long> {
	List<User> findByUsername(String username);
}
