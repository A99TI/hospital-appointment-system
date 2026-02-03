package com.hospital.system.appointments.controller;

import com.hospital.system.appointments.response.UserResponse;
import com.hospital.system.appointments.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin REST API Endpoints", description = "Operations related to a admin")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Get all users", description = "Retrieve a list of all users in the system")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users")
    public List<UserResponse> getAllUsers(){
        return adminService.getAllUsers();
    }

    @Operation(summary = "Delete a user", description = "Delete a non-admin user from the system")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable @Min(1) long userId){
        adminService.deleteNonAdminUser(userId);
    }


}
