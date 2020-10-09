package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.SaltGenerator;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject){
        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);

            if(!f.isAccessible()){
                f.setAccessible(true);
                wasPrivate = true;
            }

            f.set(target, toInject);

            if(wasPrivate){
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void createUser(UserRepository userRepository){
        SaltGenerator saltGenerator = new SaltGenerator();
        byte[] salt = saltGenerator.createSalt();
        String password = saltGenerator.getSecurePassword("password", salt);
        User user = User.builder()
                .id(1L)
                .username("user2")
                .password(password)
                .salt(salt)
                .cart(new Cart())
                .build();
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("user2")).thenReturn(user);
        userRepository.save(user);
    }
}
