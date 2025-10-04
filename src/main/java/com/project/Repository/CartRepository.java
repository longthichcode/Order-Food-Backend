package com.project.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Entity.Cart;
import com.project.Entity.User;

public interface CartRepository extends JpaRepository<Cart, Integer> {
	Optional<Cart> findByUser(User user);

}
