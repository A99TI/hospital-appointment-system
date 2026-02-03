package com.hospital.system.appointments.service.admin;

import com.hospital.system.appointments.request.DoctorRequest;
import com.hospital.system.appointments.response.DoctorResponse;
import com.hospital.system.appointments.response.UserResponse;

public interface UserRoleAdminService {
    UserResponse promoteToAdmin(long userId);
    DoctorResponse promoteToDoctor (long userId, DoctorRequest doctorRequest);
}
