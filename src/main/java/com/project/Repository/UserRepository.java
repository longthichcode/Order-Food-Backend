package com.project.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.Entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByUsername(String username);
	
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	boolean existsByPhone(String phone);
	
	@Query(value = "UPDATE users SET fullname = :fullname, email = :email, phone = :phone WHERE id = :id", nativeQuery = true)
	boolean updateUserInfo(Integer id, String fullname, String email, String phone);
	
	// sửa vai trò người dùng
	@Query(value = "UPDATE users SET role = :role WHERE id = :id", nativeQuery = true)
	boolean updateUserRole(Integer id, String role);
}
