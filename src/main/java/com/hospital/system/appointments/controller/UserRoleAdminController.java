package com.hospital.system.appointments.controller;

import com.hospital.system.appointments.request.DoctorRequest;
import com.hospital.system.appointments.response.DoctorResponse;
import com.hospital.system.appointments.response.UserResponse;
import com.hospital.system.appointments.service.admin.UserRoleAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin User Promotion REST API Endpoint", description = "Operations related to a admin")
@RestController
@RequestMapping("/api/admin/users")
public class UserRoleAdminController {

    public final UserRoleAdminService userRoleAdminService;

    public UserRoleAdminController(UserRoleAdminService userRoleAdminService) {
        this.userRoleAdminService = userRoleAdminService;
    }

    @Operation(summary = "Promote user to admin", description = "Promote user to admin role")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/promote-to-admin")
    public UserResponse promoteToAdmin(@PathVariable @Min(1) long userId){
        return userRoleAdminService.promoteToAdmin(userId);
    }


    @Operation(summary = "Promote user to doctor", description = "Promote user to doctor role")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/promote-to-doctor")
    public DoctorResponse promoteToDoctor(@PathVariable @Min(1) long userId, @Valid @RequestBody DoctorRequest doctorRequest){
        return userRoleAdminService.promoteToDoctor(userId, doctorRequest);
    }

}
