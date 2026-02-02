package com.hospital.system.appointments.request;

import com.hospital.system.appointments.enums.DayOfWeek;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ScheduleRequest {

    @NotNull(message = "Day of week is mandatory")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is mandatory")
    private LocalTime startTime;

    @NotNull(message = "End time is mandatory")
    private LocalTime endTime;

    @NotNull(message = "Max Patients is mandatory")
    @Min(value = 1, message = "Max Patients must be at least 1")
    private Integer maxPatients;
}
