package com.hospital.system.appointments.service;

import com.hospital.system.appointments.entity.Authority;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.repository.UserRepository;
import com.hospital.system.appointments.response.UserResponse;
import com.hospital.system.appointments.util.FindNonAdminUser;
import com.hospital.system.appointments.util.UserMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class UserRoleAdminServiceImpl implements UserRoleAdminService{

    private final UserRepository userRepository;
    private final FindNonAdminUser findNonAdminUser;
    private final UserMapper userMapper;

    public UserRoleAdminServiceImpl(UserRepository userRepository, FindNonAdminUser findNonAdminUser, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.findNonAdminUser = findNonAdminUser;
        this.userMapper = userMapper;
    }


    @Override
    @Transactional
    public UserResponse promoteToAdmin(long userId) {
        User user = findNonAdminUser.getNonAdminUser(userId);

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_USER"));
        authorities.add(new Authority("ROLE_ADMIN"));
        user.setAuthorities(authorities);

        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }
}
