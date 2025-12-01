package com.project.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.Entity.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	@Query(value = "UPDATE users SET fullname = :fullname, email = :email, phone = :phone WHERE id = :id", nativeQuery = true)
	boolean updateUserInfo(Integer id, String fullname, String email, String phone);

	// sửa vai trò người dùng
	@Modifying
	@Transactional
	@Query(value = "UPDATE users SET role = :role WHERE user_id = :id", nativeQuery = true)
	boolean updateUserRole(Integer id, String role);

	// khoá tài khoản người dùng
	@Modifying
	@Transactional
	@Query(value = "UPDATE users SET status = false WHERE user_id = :id", nativeQuery = true)
	int disableUserAccount(@Param("id") Integer id);

	//mở khoá tài khoản người dùng
	@Modifying
	@Transactional
	@Query(value = "UPDATE users SET status = true WHERE user_id = :id", nativeQuery = true)
	int undisableUserAccount(@Param("id") Integer id);
}
