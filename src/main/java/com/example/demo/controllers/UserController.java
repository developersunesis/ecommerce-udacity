package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.SaltGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private SaltGenerator saltGenerator;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {

		if(!createUserRequest.getConfirmPassword().equals(createUserRequest.getPassword())){
			log.error("User creation failed due to passwords do not match!");
			return ResponseEntity.badRequest().build();
		}

		if(createUserRequest.getConfirmPassword().length() < 7 ||
			createUserRequest.getPassword().length() < 7){
			log.error("User creation failed due to passwords length is invalid!");
			return ResponseEntity.badRequest().build();
		}

		if(userRepository.findByUsername(createUserRequest.getUsername()) != null){
			log.error("User creation failed due to existing username " +
					createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		byte[] salt = saltGenerator.createSalt();
		String password = saltGenerator.getSecurePassword(createUserRequest.getPassword(), salt);
		user.setPassword(bCryptPasswordEncoder.encode(password));
		user.setSalt(salt);

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		userRepository.save(user);

		log.info("User successfully created!");
		return ResponseEntity.ok(user);
	}
}
