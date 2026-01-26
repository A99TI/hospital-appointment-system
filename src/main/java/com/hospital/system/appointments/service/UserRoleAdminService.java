package com.hospital.system.appointments.service;

import com.hospital.system.appointments.response.UserResponse;

public interface UserRoleAdminService {
    UserResponse promoteToAdmin(long userId);

}
