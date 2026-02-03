package com.hospital.system.appointments.service.admin;

import com.hospital.system.appointments.response.UserResponse;

import java.util.List;

public interface AdminService {
    List<UserResponse> getAllUsers();
    void deleteNonAdminUser(long userId);
}
