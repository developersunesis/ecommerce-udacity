package com.example.demo.impl;

import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.SaltGenerator;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private SaltGenerator saltGenerator;

    public UserDetailsServiceImpl(UserRepository userRepository, SaltGenerator saltGenerator) {
        this.userRepository= userRepository;
        this.saltGenerator = saltGenerator;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.demo.model.persistence.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(user.getUsername(), user.getPassword(), emptyList());
    }

    public String getUserSaltedPassword(String username, String password) throws UsernameNotFoundException {
        com.example.demo.model.persistence.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return saltGenerator.getSecurePassword(password, user.getSalt());
    }
}