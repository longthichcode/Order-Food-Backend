package com.project.Controller;

import com.project.Config.JwtUtil;
import com.project.Entity.User;
import com.project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							authRequest.getUsername(),
							authRequest.getPassword()
					)
			);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Incorrect username or password");
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
		final String jwt = jwtUtil.generateToken(userDetails.getUsername());

		// Lấy thông tin user từ DB
		User user = userRepository.findByUsername(authRequest.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Trả về jwt + thông tin user
		return ResponseEntity.ok(new AuthResponse(jwt, user));
	}
}

class AuthRequest {
	private String username;
	private String password;

	// Getters and setters
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

class AuthResponse {
	private final String jwt;
	private final int userId;
	private final String username;
	private final String email;
	private final String role;

	public AuthResponse(String jwt, User user) {
		this.jwt = jwt;
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.role = user.getRole().name();
	}

	public String getJwt() {
		return jwt;
	}
	public int getUserId() {
		return userId;
	}
	public String getUsername() {
		return username;
	}
	public String getEmail() {
		return email;
	}
	public String getRole() {
		return role;
	}
}
