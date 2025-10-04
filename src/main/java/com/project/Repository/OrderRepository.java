package com.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
