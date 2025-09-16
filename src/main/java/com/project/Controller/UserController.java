package com.project.Controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.DTO.ErrDTO;
import com.project.Entity.User;
import com.project.Entity.User.Role;
import com.project.Repository.UserRepository;
import com.project.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200/")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // Lấy tất cả người dùng
    @GetMapping
    public Iterable<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Lấy người dùng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id, HttpServletRequest request) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrDTO("User not found", request.getServletPath()));
        }
        return ResponseEntity.ok(user);
    }

    // Đăng ký (Thêm mới user)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user, HttpServletRequest request) {
        // role mặc định là CUSTOMER
        user.setRole(Role.CUSTOMER);
        //lấy ngày hiện tại
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        // Check trùng username, email, phone
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrDTO("Username already exists!", request.getServletPath()));
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrDTO("Email already exists!", request.getServletPath()));
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrDTO("Phone number already exists!", request.getServletPath()));
        }

        // 🔒 Mã hoá mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        boolean success = userService.addUser(user);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrDTO("Failed to register user", request.getServletPath()));
    }

    // Cập nhật user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user, HttpServletRequest request) {
        User existing = userService.getUserById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrDTO("User not found", request.getServletPath()));
        }

        // Check trùng username, email, phone (trừ chính user đó)
        if (!existing.getUsername().equals(user.getUsername()) && userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrDTO("Username already exists!", request.getServletPath()));
        }
        if (!existing.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrDTO("Email already exists!", request.getServletPath()));
        }
        if (!existing.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(user.getPhone())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrDTO("Phone number already exists!", request.getServletPath()));
        }

        // 🔒 Nếu có mật khẩu mới thì mã hoá, không thì giữ nguyên
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }

        user.setUserId(id);
        boolean success = userService.updateUser(user);
        if (success) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrDTO("Failed to update user", request.getServletPath()));
    }

    // Xoá user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id, HttpServletRequest request) {
        User existing = userService.getUserById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrDTO("User not found", request.getServletPath()));
        }

        boolean success = userService.deleteUser(id);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrDTO("Failed to delete user", request.getServletPath()));
    }
}
