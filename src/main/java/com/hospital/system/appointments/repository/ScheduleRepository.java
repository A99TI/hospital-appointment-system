package com.hospital.system.appointments.repository;

import com.hospital.system.appointments.entity.Schedule;
import com.hospital.system.appointments.enums.DayOfWeek;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<Schedule, Long> {

    List<Schedule> findByDoctorId(long doctorId);

    List<Schedule> findByDoctorIdAndDayOfWeek(long doctorId, DayOfWeek dayOfWeek);

    List<Schedule> findByDoctorIdAndDayOfWeekAndIdNot(Long doctorId, DayOfWeek dayOfWeek, Long excludeId);

}
