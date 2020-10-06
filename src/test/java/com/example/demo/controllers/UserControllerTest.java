package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.SaltGenerator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private final SaltGenerator saltGenerator = mock(SaltGenerator.class);

    @Before
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
        createUserRequest.setConfirmPassword("password");

        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals(createUserRequest.getUsername(), user.getUsername());
        assertEquals(createUserRequest.getPassword(), user.getPassword());
    }
}
