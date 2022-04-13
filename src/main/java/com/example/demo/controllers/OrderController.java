package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;




	private Logger logger = LoggerFactory.getLogger(OrderController.class);





	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		//get the username
		User user = userRepository.findByUsername(username);
		//from repository of userRepository
		if(user == null) {
			//if their is no user present
			//lets print an erroe msg taking from logger
			//	logger.error("unable to find the user ");
			//if we want to alaong with the name
			logger.error(" user not found {}", username);
			//print error message along with request username

			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		//go to crt and get the datails of items
		//show the user that this is the  item  you added
		logger.debug("Order to submit is {}", order);
		//after saving into cart u get order saved successfully
		logger.info("Order saved successfully");

		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		//lets go with the old customer
		User user = userRepository.findByUsername(username);
		//find username from  userRepository
		if(user == null) {
			//if he is new customer
			logger.error("user not found {}", username);
			//print error message as no user found with this name

			return ResponseEntity.notFound().build();
		}
		//if he is actually a old customer get his orders by his user name
		logger.info("processing all requested orders for user {}", username);

		return ResponseEntity.ok(orderRepository.findByUser(user));
		//from orderRepository return his orders
	}
}
