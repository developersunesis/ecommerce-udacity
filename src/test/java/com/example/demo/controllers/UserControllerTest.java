package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.SaltGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

public class UserControllerTest {

    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private final SaltGenerator saltGenerator = mock(SaltGenerator.class);

    @BeforeEach
    public void init(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
        TestUtils.injectObjects(userController, "saltGenerator", saltGenerator);
    }

    @Test
    public void createUser(){
        byte[] salt = saltGenerator.createSalt();
        String password = saltGenerator.getSecurePassword("password", salt);
        when(bCryptPasswordEncoder.encode(password)).thenReturn("password");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("user");
        createUserRequest.setPassword("password");

        // wrong confirm password test
        createUserRequest.setConfirmPassword("wrongpassword");

        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST.value(), responseEntity.getStatusCodeValue());

        // correct confirm password test
        createUserRequest.setConfirmPassword("password");
        responseEntity = userController.createUser(createUserRequest);
        assertNotNull(responseEntity);
        assertEquals(OK.value(), responseEntity.getStatusCodeValue());

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals(createUserRequest.getUsername(), user.getUsername());
        assertEquals(createUserRequest.getPassword(), user.getPassword());
    }

    @Test
    public void getUserByUsername(){
        TestUtils.createUser(userRepository);

        // valid existing user
        ResponseEntity<User> responseEntity = userController.findByUserName("user2");
        assertNotNull(responseEntity);
        assertEquals(OK.value(), responseEntity.getStatusCodeValue());

        // invalid non-existing user
        responseEntity = userController.findByUserName("user23");
        assertNotNull(responseEntity);
        assertEquals(NOT_FOUND.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void getUserById(){
        TestUtils.createUser(userRepository);

        // valid existing user
        ResponseEntity<User> responseEntity = userController.findById(1L);
        assertNotNull(responseEntity);
        assertEquals(OK.value(), responseEntity.getStatusCodeValue());

        // invalid non-existing user
        responseEntity = userController.findById(2L);
        assertNotNull(responseEntity);
        assertEquals(NOT_FOUND.value(), responseEntity.getStatusCodeValue());
    }
}
