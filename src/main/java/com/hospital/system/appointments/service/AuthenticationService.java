package com.hospital.system.appointments.service;

import com.hospital.system.appointments.request.RegisterRequest;

public interface AuthenticationService {
    void register (RegisterRequest registerRequest) throws Exception;
}
