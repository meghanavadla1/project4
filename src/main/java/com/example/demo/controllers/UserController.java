package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;





	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	///////////////////////////////////////
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {

		User user = userRepository.findByUsername(username);
		//return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
		if(user == null){
			//as per the above statement asked if user is not present
			//print user not found
			logger.info("Unable to find user with username {}", username);
			return ResponseEntity.notFound().build();
		}else{
			return ResponseEntity.ok(user);
			//if present save it
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		logger.info("user name set with",createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		/////////////////////////////////////////////////

		if(createUserRequest.getPassword().length() < 7 ||
			!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			//we gave here two conditions as either the length should be less then 7 letters and it should match with
			//confirmed password


			//if it is not matching print error message

			logger.error("Password doesnt meet complexity requirement or confirm password is not same");
			return ResponseEntity.badRequest().build();
			//return error
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		//if coorect set the user password
		userRepository.save(user);
		//save the user
		logger.info("Created user with username {}",user.getUsername());
		//print the user account is created with this username

		return ResponseEntity.ok(user);
	}
	
}
