package com.hospital.system.appointments.service;

import com.hospital.system.appointments.entity.Authority;
import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.enums.Role;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.repository.DoctorRepository;
import com.hospital.system.appointments.repository.UserRepository;
import com.hospital.system.appointments.request.DoctorRequest;
import com.hospital.system.appointments.response.DoctorResponse;
import com.hospital.system.appointments.response.UserResponse;
import com.hospital.system.appointments.util.FindNonAdminUser;
import com.hospital.system.appointments.util.ResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRoleAdminServiceImpl implements UserRoleAdminService {

    private final UserRepository userRepository;
    private final FindNonAdminUser findNonAdminUser;
    private final ResponseMapper responseMapper;
    private final DoctorRepository doctorRepository;

    public UserRoleAdminServiceImpl(UserRepository userRepository, FindNonAdminUser findNonAdminUser, ResponseMapper responseMapper, DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.findNonAdminUser = findNonAdminUser;
        this.responseMapper = responseMapper;
        this.doctorRepository = doctorRepository;
    }

    @Override
    @Transactional
    public UserResponse promoteToAdmin(long userId) {
        User user = findNonAdminUser.getNonAdminUser(userId);

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(Role.USER.getAuthority()));
        authorities.add(new Authority(Role.ADMIN.getAuthority()));
        user.setAuthorities(authorities);

        User savedUser = userRepository.save(user);

        return responseMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public DoctorResponse promoteToDoctor(long userId, DoctorRequest doctorRequest) {
        User user = findNonAdminUser.getNonAdminUser(userId);

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(Role.USER.getAuthority()));
        authorities.add(new Authority(Role.DOCTOR.getAuthority()));
        user.setAuthorities(authorities);
        User savedUser = userRepository.save(user);

        boolean active = doctorRequest.getActive() != null ? doctorRequest.getActive() : true;

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setFullName(doctorRequest.getFullName());
        doctor.setSpecialisation(doctorRequest.getSpecialisation());
        doctor.setRoomNumber(doctorRequest.getRoomNumber());
        doctor.setActive(active);

        Doctor savedDoctor = doctorRepository.save(doctor);
        return responseMapper.toDoctorResponse(savedDoctor);

    }
}
