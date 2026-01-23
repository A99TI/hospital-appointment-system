package com.hospital.system.appointments.service;

import com.hospital.system.appointments.response.UserResponse;

import java.util.List;

public interface AdminService {
    List<UserResponse> getAllUsers();
    void deleteNonAdminUser(long userId);
}
