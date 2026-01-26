package com.hospital.system.appointments.repository;

import com.hospital.system.appointments.entity.Doctor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends CrudRepository<Doctor, Long> {
}
