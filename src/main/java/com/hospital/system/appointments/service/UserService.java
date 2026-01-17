package com.hospital.system.appointments.service;

import com.hospital.system.appointments.request.PasswordUpdateRequest;
import com.hospital.system.appointments.response.UserResponse;

public interface UserService {
    UserResponse getUser();
    void deleteUser();
    void updatePassword (PasswordUpdateRequest passwordUpdateRequest);
}
