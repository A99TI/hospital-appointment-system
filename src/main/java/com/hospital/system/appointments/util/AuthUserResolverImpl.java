package com.hospital.system.appointments.util;

import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.enums.Role;
import com.hospital.system.appointments.exception.ConflictException;
import com.hospital.system.appointments.exception.NotFoundException;
import com.hospital.system.appointments.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@AllArgsConstructor
@Component
public class AuthUserResolverImpl implements AuthUserResolver{

    private final UserRepository userRepository;

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication Required");
        }

        return (User) authentication.getPrincipal();
    }

    @Override
    public User getNonAdminUser(long userId){
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) throw new NotFoundException("User does not exist");
        if (user.get().getAuthorities().stream().anyMatch(authority -> Role.ADMIN.getAuthority().equals(authority.getAuthority()))) {
            throw new ConflictException("User is an admin");
        }

        return user.get();
    }
}
