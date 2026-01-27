package com.hospital.system.appointments.service;

import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.entity.Schedule;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.enums.DayOfWeek;
import com.hospital.system.appointments.exception.BadRequestException;
import com.hospital.system.appointments.exception.ConflictException;
import com.hospital.system.appointments.exception.ForbiddenException;
import com.hospital.system.appointments.exception.NotFoundException;
import com.hospital.system.appointments.repository.DoctorRepository;
import com.hospital.system.appointments.repository.ScheduleRepository;
import com.hospital.system.appointments.request.ScheduleRequest;
import com.hospital.system.appointments.response.ScheduleResponse;
import com.hospital.system.appointments.util.FindAuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
public class ScheduleServiceImpl implements ScheduleService{

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, DoctorRepository doctorRepository, FindAuthenticatedUser findAuthenticatedUser) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
    }

    @Override
    @Transactional
    public ScheduleResponse createSchedule(long doctorId, ScheduleRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with id: " + doctorId));

        User user = findAuthenticatedUser.getAuthenticatedUser();
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (!isAdmin && !Objects.equals(user.getId(), doctor.getUser().getId())) {
            throw new ForbiddenException("Cannot make schedule for this user");
        }

        if (!doctor.getActive()){
            throw new BadRequestException("Cannot create schedule for inactive doctor");
        }

        validTimeRange(request.getStartTime(), request.getEndTime());

        checkForOverLaps(doctor, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), null);

        Schedule schedule = new Schedule(
                doctor,
                request.getDayOfWeek(),
                request.getStartTime(),
                request.getEndTime(),
                request.getMaxPatients()
        );

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return toScheduleResponse(savedSchedule);
    }

    @Override
    public ScheduleResponse updateSchedule(long doctorId, Long scheduleId, ScheduleRequest request) {
        return null;
    }

    @Override
    public void deleteSchedule(long doctorId, Long scheduleId) {

    }

    @Override
    public List<ScheduleResponse> getScheduleByDoctorId(long doctorId) {
        return null;
    }

    @Override
    public List<ScheduleResponse> getMySchedules() {
        return List.of();
    }

    private  void validTimeRange(LocalTime startTime, LocalTime endTime){
        if (startTime.isAfter(endTime) || startTime.equals(endTime)){
            throw new BadRequestException("Start Time must be before end time");
        }
    }

    private void checkForOverLaps(Doctor doctor, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Long excludeScheduleId){
        List<Schedule> existingSchedules = excludeScheduleId == null
                ? scheduleRepository.findByDoctorIdAndDayOfWeek(doctor.getId(), dayOfWeek)
                : scheduleRepository.findByDoctorIdAndDayOfWeekAndIdNot(doctor.getId(), dayOfWeek, excludeScheduleId);

        for (Schedule existing: existingSchedules){
            if (hasOverLaps(startTime, endTime, existing.getStartTime(), existing.getEndTime())){
                throw new ConflictException(
                        String.format(
                                "Schedule overlaps with existing schedule: %s %s-%s",
                                existing.getDayOfWeek(),
                                existing.getStartTime(),
                                existing.getEndTime()
                        )
                );
            }
        }
    }

    private boolean hasOverLaps(LocalTime newStart, LocalTime newEnd, LocalTime existingStart, LocalTime existingEnd ){
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }

    private ScheduleResponse toScheduleResponse(Schedule schedule){
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
