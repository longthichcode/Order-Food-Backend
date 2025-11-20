package com.project.Service;

import org.springframework.stereotype.Service;

import com.project.Entity.User;
import com.project.Repository.UserRepository;

@Service
public class UserService {
	UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// Lấy tất cả người dùng
	public Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}

	// Thêm người dùng mới
	public boolean addUser(User user) {
		try {
			userRepository.save(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// sửa thông tin người dùng
	public boolean updateUser(User user) {
		try {
			userRepository.save(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// lấy người dùng theo ID
	public User getUserById(int userId) {
		return userRepository.findById(userId).orElse(null);
	}

	// xoá người dùng theo ID
	public boolean deleteUser(int userId) {
		try {
			userRepository.deleteById(userId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// sửa vai trò người dùng
	public boolean updateUserRole(Integer id, String role) {
		try {
			return userRepository.updateUserRole(id, role);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
