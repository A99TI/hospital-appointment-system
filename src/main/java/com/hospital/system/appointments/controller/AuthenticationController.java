package com.hospital.system.appointments.controller;

import com.hospital.system.appointments.request.RegisterRequest;
import com.hospital.system.appointments.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication REST API Endpoints", description = "Operations related to register & login")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation()
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest registerRequest) throws  Exception{
        authenticationService.register(registerRequest);
    }
}
