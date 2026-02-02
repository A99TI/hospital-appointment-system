package com.hospital.system.appointments.service.schedule;

import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.entity.Schedule;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.enums.Role;
import com.hospital.system.appointments.exception.ForbiddenException;
import com.hospital.system.appointments.exception.NotFoundException;
import com.hospital.system.appointments.repository.DoctorRepository;
import com.hospital.system.appointments.repository.ScheduleRepository;
import com.hospital.system.appointments.request.ScheduleRequest;
import com.hospital.system.appointments.response.ScheduleResponse;
import com.hospital.system.appointments.service.schedule.util.ScheduleDoctorValidator;
import com.hospital.system.appointments.service.schedule.util.ScheduleTimeValidator;
import com.hospital.system.appointments.util.FindAuthenticatedUser;
import com.hospital.system.appointments.util.ResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService{

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final ResponseMapper responseMapper;
    private final ScheduleDoctorValidator scheduleDoctorValidator;
    private final ScheduleTimeValidator scheduleTimeValidator;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, DoctorRepository doctorRepository, FindAuthenticatedUser findAuthenticatedUser, ResponseMapper responseMapper, ScheduleDoctorValidator scheduleDoctorValidator, ScheduleTimeValidator scheduleTimeValidator) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.responseMapper = responseMapper;
        this.scheduleDoctorValidator = scheduleDoctorValidator;
        this.scheduleTimeValidator = scheduleTimeValidator;
    }

    @Override
    @Transactional
    public ScheduleResponse createSchedule(long doctorId, ScheduleRequest request) {
        Doctor doctor = scheduleDoctorValidator.validateAndGetDoctor(doctorId);

        scheduleTimeValidator.validTimeRange(request.getStartTime(), request.getEndTime());

        scheduleTimeValidator.checkForOverLaps(doctor, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), null);

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
        Doctor doctor = scheduleDoctorValidator.validateAndGetDoctor(doctorId);
        Schedule schedule = scheduleDoctorValidator.findAndValidateCanManageSchedule(doctor, scheduleId);

        scheduleTimeValidator.validTimeRange(request.getStartTime(), request.getEndTime());
        Doctor scheduleOwner = schedule.getDoctor();
        scheduleTimeValidator.checkForOverLaps(scheduleOwner, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), scheduleId);

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
        Doctor doctor = scheduleDoctorValidator.validateAndGetDoctor(doctorId);
        Schedule schedule = scheduleDoctorValidator.findAndValidateCanManageSchedule(doctor, scheduleId);

        scheduleRepository.delete(schedule);
    }

    @Override
    public List<ScheduleResponse> getScheduleByDoctorId(long doctorId) {
        Doctor doctor = scheduleDoctorValidator.validateAndGetDoctor(doctorId);

        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(responseMapper::toScheduleResponse).toList();

    }

    @Override
    public List<ScheduleResponse> getMySchedules() {
        User authenticatedUser = findAuthenticatedUser.getAuthenticatedUser();

        boolean isDoctor = authenticatedUser.getAuthorities().stream()
                .anyMatch(a -> Role.DOCTOR.getAuthority().equals(a.getAuthority()));
        if (!isDoctor) {
            throw new ForbiddenException("User is not a Doctor");
        }

        Doctor doctor = doctorRepository.findByUserId(authenticatedUser.getId())
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        return scheduleRepository.findByDoctorId(doctor.getId()).stream()
                .map(responseMapper::toScheduleResponse).toList();
    }







}
