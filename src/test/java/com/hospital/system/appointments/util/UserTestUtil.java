package com.hospital.system.appointments.util;


import com.hospital.system.appointments.entity.Authority;
import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.enums.Role;
import com.hospital.system.appointments.repository.DoctorRepository;
import com.hospital.system.appointments.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserTestUtil {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public User createUser(int userNum) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(Role.USER.getAuthority()));

        return this.createAndSaveUser(userNum, authorities);
    }

    public User createAdmin(int userNum) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(Role.USER.getAuthority()));
        authorities.add(new Authority(Role.ADMIN.getAuthority()));

        return this.createAndSaveUser(userNum, authorities);
    }

    public Doctor createDoctor(int userNum) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(Role.USER.getAuthority()));
        authorities.add(new Authority(Role.DOCTOR.getAuthority()));

        User user = this.createAndSaveUser(userNum, authorities);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setFullName("user "+userNum);
        doctor.setSpecialisation("general");
        doctor.setRoomNumber("room"+userNum);
        doctor.setActive(true);

        return doctorRepository.save(doctor);

    }

    private User createAndSaveUser(int userNum,  List<Authority> authorities){
        User user = new User();
        user.setEmail("user" + userNum + "@email.com");
        user.setPassword(passwordEncoder.encode("password" + userNum));

        user.setAuthorities(authorities);

        return userRepository.save(user);
    }

    public UsernamePasswordAuthenticationToken createAuthenticationToken(User user){
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }
}
