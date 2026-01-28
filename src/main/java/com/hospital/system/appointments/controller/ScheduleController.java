package com.hospital.system.appointments.controller;


import com.hospital.system.appointments.request.ScheduleRequest;
import com.hospital.system.appointments.response.ScheduleResponse;
import com.hospital.system.appointments.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@Tag(name = "Doctor Schedule REST API Endpoints", description = "Operations related to doctor schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Operation(summary = "Create a schedule", description = "Create a schedule for a doctor")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("{doctorId}/schedules")
    public ScheduleResponse createSchedule(@PathVariable @Min(1) long doctorId, @Valid @RequestBody ScheduleRequest scheduleRequest){
        return scheduleService.createSchedule(doctorId, scheduleRequest);
    }
}
