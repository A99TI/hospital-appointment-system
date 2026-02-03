package com.hospital.system.appointments.service.authentication;

import com.hospital.system.appointments.request.AuthenticationRequest;
import com.hospital.system.appointments.request.RegisterRequest;
import com.hospital.system.appointments.response.AuthenticationResponse;

public interface AuthenticationService {
    void register (RegisterRequest registerRequest) throws Exception;
    AuthenticationResponse login (AuthenticationRequest authenticationRequest);
}
