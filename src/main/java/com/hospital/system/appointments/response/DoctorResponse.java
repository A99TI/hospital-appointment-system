package com.hospital.system.appointments.response;

import com.hospital.system.appointments.entity.Authority;
import lombok.Data;

import java.util.List;

@Data
public class DoctorResponse {
    private Long id;
    private Long userId;
    private String email;
    private String fullName;
    private String specialisation;
    private String roomNumber;
    private boolean active;
    private List<Authority> authorities;
}
