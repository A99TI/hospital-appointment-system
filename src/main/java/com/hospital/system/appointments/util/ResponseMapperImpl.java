package com.hospital.system.appointments.util;

import com.hospital.system.appointments.entity.Authority;
import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.entity.Schedule;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.response.DoctorResponse;
import com.hospital.system.appointments.response.ScheduleResponse;
import com.hospital.system.appointments.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class ResponseMapperImpl implements ResponseMapper {
    @Override
    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getAuthorities().stream().map(authority -> (Authority) authority).toList()
        );
    }

    @Override
    public DoctorResponse toDoctorResponse(Doctor doctor) {
        User user = doctor.getUser();
        DoctorResponse response = new DoctorResponse();
        response.setId(doctor.getId());
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(doctor.getFullName());
        response.setSpecialisation(doctor.getSpecialisation());
        response.setRoomNumber(doctor.getRoomNumber());
        response.setActive(doctor.getActive());
        response.setAuthorities(user.getAuthorities().stream()
                .map(authority -> (Authority) authority)
                .toList());
        return response;
    }

    @Override
    public ScheduleResponse toScheduleResponse(Schedule schedule){
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getDoctor().getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getMaxPatients()
        );
    }


}
