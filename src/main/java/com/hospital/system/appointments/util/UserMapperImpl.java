package com.hospital.system.appointments.util;

import com.hospital.system.appointments.entity.Authority;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.response.UserResponse;

public class UserMapperImpl implements UserMapper{
    @Override
    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getAuthorities().stream().map(authority -> (Authority) authority).toList()
        );
    }

}
