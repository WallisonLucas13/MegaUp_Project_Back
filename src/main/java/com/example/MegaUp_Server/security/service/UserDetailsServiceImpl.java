package com.example.MegaUp_Server.security.service;

import com.example.MegaUp_Server.security.model.UserModel;
import com.example.MegaUp_Server.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserModel userModel = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(""));

        return new User(
                userModel.getUsername(),
                userModel.getPassword(), true, true, true, true,
                userModel.getAuthorities()
        );

    }
}
