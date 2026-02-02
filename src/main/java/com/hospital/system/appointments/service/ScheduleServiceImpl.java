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
import com.hospital.system.appointments.util.ResponseMapper;
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
    private final ResponseMapper responseMapper;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, DoctorRepository doctorRepository, FindAuthenticatedUser findAuthenticatedUser, ResponseMapper responseMapper) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.responseMapper = responseMapper;
    }
    
    @Override
    @Transactional
    public ScheduleResponse createSchedule(long doctorId, ScheduleRequest request) {
        Doctor doctor = validateAndGetDoctor(doctorId);

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

        return responseMapper.toScheduleResponse(savedSchedule);
    }

    @Override
    @Transactional
    public ScheduleResponse updateSchedule(long doctorId, Long scheduleId, ScheduleRequest request) {
        Doctor doctor = validateAndGetDoctor(doctorId);
        Schedule schedule = validateCanManageSchedule(doctor, scheduleId)

        validTimeRange(request.getStartTime(), request.getEndTime());
        Doctor scheduleOwner = schedule.getDoctor();
        checkForOverLaps(scheduleOwner, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), scheduleId);

        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setMaxPatients(request.getMaxPatients());

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return responseMapper.toScheduleResponse(updatedSchedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(long doctorId, Long scheduleId) {
        Doctor doctor = validateAndGetDoctor(doctorId);
        Schedule schedule = validateCanManageSchedule(doctor, scheduleId);

        scheduleRepository.delete(schedule);
    }

    @Override
    public List<ScheduleResponse> getScheduleByDoctorId(long doctorId) {
        Doctor doctor = validateAndGetDoctor(doctorId);

        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(responseMapper::toScheduleResponse).toList();

    }

    @Override
    public List<ScheduleResponse> getMySchedules() {
        User authenticatedUser = findAuthenticatedUser.getAuthenticatedUser();

        boolean isDoctor = authenticatedUser.getAuthorities().stream()
                .anyMatch(a -> "ROLE_DOCTOR".equals(a.getAuthority()));
        if (!isDoctor) {
            throw new ForbiddenException("User is not a Doctor");
        }

        Doctor doctor = doctorRepository.findByUserId(authenticatedUser.getId())
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        return scheduleRepository.findByDoctorId(doctor.getId()).stream()
                .map(responseMapper::toScheduleResponse).toList();
    }

    private Schedule validateCanManageSchedule(Doctor doctor, long scheduleId) {
        User authenticatedUser = findAuthenticatedUser.getAuthenticatedUser();

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found with id: " + scheduleId));

        if (!isAdmin(authenticatedUser) && !Objects.equals(schedule.getDoctor().getId(), doctor.getId())) {
            throw new NotFoundException("You do not have permission to manage this doctor's schedules");
        }

        return schedule;
    }

    private Doctor validateAndGetDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with id: " + doctorId));

        User authenticatedUser = findAuthenticatedUser.getAuthenticatedUser();

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
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private boolean isOwnDoctor(User user, Doctor doctor) {
        return Objects.equals(user.getId(), doctor.getUser().getId());
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



}
