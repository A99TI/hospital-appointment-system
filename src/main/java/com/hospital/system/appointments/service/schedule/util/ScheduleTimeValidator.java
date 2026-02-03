package com.hospital.system.appointments.service.schedule.util;

import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.entity.Schedule;
import com.hospital.system.appointments.enums.DayOfWeek;
import com.hospital.system.appointments.exception.BadRequestException;
import com.hospital.system.appointments.exception.ConflictException;
import com.hospital.system.appointments.repository.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
@AllArgsConstructor
public class ScheduleTimeValidator {

    private final ScheduleRepository scheduleRepository;

    public void validateTimeRange(LocalTime startTime, LocalTime endTime){
        if (startTime.isAfter(endTime) || startTime.equals(endTime)){
            throw new BadRequestException("Start Time must be before end time");
        }
    }

    public void checkForOverlaps(Doctor doctor, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Long excludeScheduleId){
        List<Schedule> existingSchedules = excludeScheduleId == null
                ? scheduleRepository.findByDoctorIdAndDayOfWeek(doctor.getId(), dayOfWeek)
                : scheduleRepository.findByDoctorIdAndDayOfWeekAndIdNot(doctor.getId(), dayOfWeek, excludeScheduleId);

        for (Schedule existing: existingSchedules){
            if (hasOverlaps(startTime, endTime, existing.getStartTime(), existing.getEndTime())){
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

    public boolean hasOverlaps(LocalTime newStart, LocalTime newEnd, LocalTime existingStart, LocalTime existingEnd ){
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }
}
