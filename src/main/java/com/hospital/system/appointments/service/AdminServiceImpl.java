package com.hospital.system.appointments.service;

import com.hospital.system.appointments.entity.Authority;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.repository.UserRepository;
import com.hospital.system.appointments.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(this::convertToUserResponse).toList();
    }

    @Override
    @Transactional
    public UserResponse promoteToAdmin(long userId) {
        User user = getNonAdminUser(userId);

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_EMPLOYEE"));
        authorities.add(new Authority("ROLE_ADMIN"));
        user.setAuthorities(authorities);

        User savedUser = userRepository.save(user);

        return convertToUserResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteNonAdminUser(long userId) {
        User user = getNonAdminUser(userId);

        userRepository.delete(user);

    }

    private UserResponse convertToUserResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getAuthorities().stream().map(authority -> (Authority) authority).toList()
        );
    }

    private User getNonAdminUser(long userId){
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        if (user.get().getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is an admin");
        }

        return user.get();
    }
}
