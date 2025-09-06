package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.project.Service.FoodService;

@SpringBootApplication
public class OrderFoodApplication {

	FoodService foodService;
	public static void main(String[] args) {
		SpringApplication.run(OrderFoodApplication.class, args);
	}

}
 