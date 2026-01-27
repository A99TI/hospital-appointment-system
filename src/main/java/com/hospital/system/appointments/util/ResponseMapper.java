package com.hospital.system.appointments.util;

import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.response.DoctorResponse;
import com.hospital.system.appointments.response.UserResponse;

public interface ResponseMapper {
    UserResponse toUserResponse(User user);
    DoctorResponse toDoctorResponse(Doctor doctor);
}
