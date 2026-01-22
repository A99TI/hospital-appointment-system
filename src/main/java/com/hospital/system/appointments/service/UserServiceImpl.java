package com.hospital.system.appointments.service;

import com.hospital.system.appointments.entity.Authority;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.exception.BadRequestException;
import com.hospital.system.appointments.exception.ForbiddenException;
import com.hospital.system.appointments.repository.UserRepository;
import com.hospital.system.appointments.request.PasswordUpdateRequest;
import com.hospital.system.appointments.response.UserResponse;
import com.hospital.system.appointments.util.FindAuthenticatedUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, FindAuthenticatedUser findAuthenticatedUser, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(){
        User user =  findAuthenticatedUser.getAuthenticatedUser();

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getAuthorities().stream().map(auth -> (Authority) auth).toList()
        );
    }

    @Override
    public void deleteUser () {
        User user =  findAuthenticatedUser.getAuthenticatedUser();

        if (isLastAdmin(user)){
            throw new ForbiddenException("Admin cannot delete itself");
        }

        userRepository.delete(user);

    }

    private boolean isLastAdmin(User user){
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (isAdmin){
            long adminCount = userRepository.countAdminUsers();
            return adminCount <= 1;
        }

        return false;
    }

    @Override
    public void updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
        User user = findAuthenticatedUser.getAuthenticatedUser();

        if (!isOldPasswordCorrect(passwordUpdateRequest.getOldPassword(), user.getPassword())){
            throw new BadRequestException("Current password is incorrect");
        }

        if (!isNewPasswordConfirmed(passwordUpdateRequest.getNewPassword(), passwordUpdateRequest.getNewPasswordConfirmation())){
            throw new BadRequestException("New password does not match");
        }

        if (!isNewPasswordDifferent(passwordUpdateRequest.getOldPassword(), passwordUpdateRequest.getNewPassword())){
            throw new BadRequestException("Old and new password must be different");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        userRepository.save(user);
    }

    private boolean isOldPasswordCorrect(String oldPassword, String currentPassword){
        return passwordEncoder.matches(oldPassword, currentPassword);
    }

    private boolean isNewPasswordConfirmed(String newPassword, String newPasswordConfirmation){
        return newPassword.equals(newPasswordConfirmation);
    }

    private boolean isNewPasswordDifferent(String oldPassword, String newPassword){
        return !oldPassword.equals(newPassword);
    }


}
