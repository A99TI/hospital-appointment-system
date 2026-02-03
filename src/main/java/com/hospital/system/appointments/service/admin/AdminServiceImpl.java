package com.hospital.system.appointments.service.admin;

import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.repository.UserRepository;
import com.hospital.system.appointments.response.UserResponse;
import com.hospital.system.appointments.util.AuthUserResolver;
import com.hospital.system.appointments.util.ResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;
    private final AuthUserResolver authUserResolver;
    private final ResponseMapper responseMapper;

    public AdminServiceImpl(UserRepository userRepository, AuthUserResolver authUserResolver, ResponseMapper responseMapper) {
        this.userRepository = userRepository;
        this.authUserResolver = authUserResolver;
        this.responseMapper = responseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(responseMapper::toUserResponse).toList();
    }

    @Override
    @Transactional
    public void deleteNonAdminUser(long userId) {
        User user = authUserResolver.getNonAdminUser(userId);

        userRepository.delete(user);

    }
}
