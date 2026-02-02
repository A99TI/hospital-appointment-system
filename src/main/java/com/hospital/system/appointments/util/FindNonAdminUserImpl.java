package com.hospital.system.appointments.util;

import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.exception.ConflictException;
import com.hospital.system.appointments.exception.NotFoundException;
import com.hospital.system.appointments.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FindNonAdminUserImpl implements FindNonAdminUser{

    private final UserRepository userRepository;

    public FindNonAdminUserImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getNonAdminUser(long userId){
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) throw new NotFoundException("User does not exist");
        if (user.get().getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            throw new ConflictException("User is an admin");
        }

        return user.get();
    }
}
