package com.hospital.system.appointments.service.schedule;

import com.hospital.system.appointments.request.ScheduleRequest;
import com.hospital.system.appointments.response.ScheduleResponse;

import java.util.List;

public interface ScheduleService {
    ScheduleResponse createSchedule(long doctorId, ScheduleRequest request);
    ScheduleResponse updateSchedule(long doctorId, Long scheduleId, ScheduleRequest request);
    void deleteSchedule (long doctorId, Long scheduleId);
    List<ScheduleResponse> getScheduleByDoctorId(long doctorId);
    List<ScheduleResponse> getMySchedules();
}
