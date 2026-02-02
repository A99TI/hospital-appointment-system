package com.hospital.system.appointments.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class DoctorRequest {

    @NotEmpty(message = "Full Name is mandatory")
    @Size(min = 1, max = 200, message = "Full Name must be between 1 and 200 characters")
    private String fullName;

    @NotEmpty(message = "Specialisation is mandatory")
    @Size(min = 1, max = 200, message = "Specialisation must be between 1 and 200 characters")
    private String specialisation;

    @NotEmpty(message = "Room Number is mandatory")
    @Size(min = 1, max = 100, message = "Room Number must be between 1 and 100 characters")
    private String roomNumber;

    private Boolean active;
}
