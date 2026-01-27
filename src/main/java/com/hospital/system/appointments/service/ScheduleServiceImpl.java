package com.hospital.system.appointments.service;

import com.hospital.system.appointments.request.ScheduleRequest;
import com.hospital.system.appointments.response.ScheduleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService{
    @Override
    public ScheduleResponse createSchedule(long doctorId, ScheduleRequest request) {
        return null;
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
}
