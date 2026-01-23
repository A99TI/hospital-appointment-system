package com.hospital.system.appointments.util;

import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.response.UserResponse;

public interface UserMapper {
    UserResponse toUserResponse(User user);
}
