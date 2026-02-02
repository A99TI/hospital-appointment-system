package com.hospital.system.appointments.enums;

public enum Role {
    ADMIN,
    USER,
    DOCTOR;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
