package com.project.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByUsername(String username);
	
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	boolean existsByPhone(String phone);
}
