package com.hospital.system.appointments.controller;


import com.hospital.system.appointments.request.PasswordUpdateRequest;
import com.hospital.system.appointments.response.UserResponse;
import com.hospital.system.appointments.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users REST API Endpoints", description = "Operations related to info about current user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user info", description = "Get information about the current user")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/info")
    public UserResponse getUserInfo() throws Exception{
        return userService.getUser();
    }


    @Operation(summary = "Delete a user", description = "Delete a user from the DB")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteUser() throws Exception{
        userService.deleteUser();
    }

    @Operation(summary = "Update user's password", description = "User can update their current password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/password")
    public void passwordUpdate(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest){
        userService.updatePassword(passwordUpdateRequest);
    }
}
