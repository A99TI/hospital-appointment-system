package com.hospital.system.appointments.service;

import com.hospital.system.appointments.entity.Authority;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.repository.UserRepository;
import com.hospital.system.appointments.request.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) throws Exception {
        if (isEmailTaken(registerRequest.getEmail())){
            throw new Exception("Email is taken");
        }
        User user = buildNewUser(registerRequest);
        userRepository.save(user);
    }

    private boolean isEmailTaken(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    private User buildNewUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAuthorities(initialAuthority());
        return user;
    }

    private List<Authority> initialAuthority() {
        boolean isFirstUser = userRepository.count() == 0;
        List<Authority> authorities = new ArrayList<>();

        if (isFirstUser) authorities.add(new Authority("ROLE_ADMIN"));
        authorities.add(new Authority("ROLE_EMPLOYEE"));

        return authorities;
    }
}
