package com.hospital.system.appointments.service.schedule.util;

import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.entity.Schedule;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.enums.Role;
import com.hospital.system.appointments.exception.BadRequestException;
import com.hospital.system.appointments.exception.ForbiddenException;
import com.hospital.system.appointments.exception.NotFoundException;
import com.hospital.system.appointments.repository.DoctorRepository;
import com.hospital.system.appointments.repository.ScheduleRepository;
import com.hospital.system.appointments.util.AuthUserResolver;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class ScheduleDoctorValidator {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final AuthUserResolver authUserResolver;


    public Schedule findAndValidateCanManageSchedule(Doctor doctor, long scheduleId) {
        User authenticatedUser = authUserResolver.getAuthenticatedUser();

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found with id: " + scheduleId));

        if (!isAdmin(authenticatedUser) && !Objects.equals(schedule.getDoctor().getId(), doctor.getId())) {
            throw new NotFoundException("You do not have permission to manage this doctor's schedules");
        }

        return schedule;
    }

    public Doctor validateAndGetDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with id: " + doctorId));

        User authenticatedUser = authUserResolver.getAuthenticatedUser();

        if (!isAdmin(authenticatedUser) && !isOwnDoctor(authenticatedUser, doctor)) {
            throw new ForbiddenException("You do not have permission to manage this doctor's schedules");
        }

        if (!doctor.getActive()) {
            throw new BadRequestException("Cannot perform this action for an inactive doctor.");
        }

        return doctor;
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(authority -> Role.ADMIN.getAuthority().equals(authority.getAuthority()));
    }

    private boolean isOwnDoctor(User user, Doctor doctor) {
        return Objects.equals(user.getId(), doctor.getUser().getId());
    }
}
