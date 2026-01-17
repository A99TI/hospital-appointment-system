package com.hospital.system.appointments.repository;

import com.hospital.system.appointments.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) from User u JOIN u.authorities a WHERE a.authority = 'ROLE_ADMIN'")
    long countAdminUsers();
}
