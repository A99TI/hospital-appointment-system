package com.hospital.system.appointments.repository;

import com.hospital.system.appointments.entity.Doctor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface DoctorRepository extends CrudRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(long userId);
}
