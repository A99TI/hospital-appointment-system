package com.hospital.system.appointments.service.admin;

import com.hospital.system.appointments.entity.Authority;
import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.enums.Role;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.repository.DoctorRepository;
import com.hospital.system.appointments.repository.UserRepository;
import com.hospital.system.appointments.request.DoctorRequest;
import com.hospital.system.appointments.response.DoctorResponse;
import com.hospital.system.appointments.response.UserResponse;
import com.hospital.system.appointments.util.AuthUserResolver;
import com.hospital.system.appointments.util.ResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRoleAdminServiceImpl implements UserRoleAdminService {

    private final UserRepository userRepository;
    private final AuthUserResolver authUserResolver;
    private final ResponseMapper responseMapper;
    private final DoctorRepository doctorRepository;

    public UserRoleAdminServiceImpl(UserRepository userRepository, AuthUserResolver authUserResolver, ResponseMapper responseMapper, DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.authUserResolver = authUserResolver;
        this.responseMapper = responseMapper;
        this.doctorRepository = doctorRepository;
    }

    @Override
    @Transactional
    public UserResponse promoteToAdmin(long userId) {
        User user = authUserResolver.getNonAdminUser(userId);

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
        User user = authUserResolver.getNonAdminUser(userId);

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
