package com.hospital.system.appointments.response;

import com.hospital.system.appointments.entity.Authority;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private Long userId;
    private String email;
    private String fullName;
    private String specialisation;
    private String roomNumber;
    private Boolean active;
    private List<Authority> authorities;
}
