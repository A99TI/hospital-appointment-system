package com.hospital.system.appointments.controller;


import com.hospital.system.appointments.request.ScheduleRequest;
import com.hospital.system.appointments.response.ScheduleResponse;
import com.hospital.system.appointments.service.schedule.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/{doctorId}/schedules")
    public ScheduleResponse createSchedule(@PathVariable @Min(1) long doctorId, @Valid @RequestBody ScheduleRequest scheduleRequest){
        return scheduleService.createSchedule(doctorId, scheduleRequest);
    }

    @Operation(summary = "Retrieve schedules", description = "Retrieve the schedule for a doctor")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{doctorId}/schedules")
    public List<ScheduleResponse> getSchedulesByDoctorId(@PathVariable @Min(1) long doctorId){
        return scheduleService.getScheduleByDoctorId(doctorId);
    }

    @Operation(summary = "Update a schedule", description = "Update a schedule for a doctor")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{doctorId}/schedules/{scheduleId}")
    public ScheduleResponse updateSchedule(@PathVariable @Min(1) long doctorId, @PathVariable @Min(1) Long scheduleId, @Valid @RequestBody ScheduleRequest scheduleRequest) {
        return scheduleService.updateSchedule(doctorId, scheduleId, scheduleRequest);
    }

    @Operation(summary = "Retrieve schedule for yourself", description = "Retrieve the schedule for logged in doctor")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me/schedules")
    public List<ScheduleResponse> getMySchedule(){
        return scheduleService.getMySchedules();
    }

    @Operation(summary = "Delete a schedule", description = "Delete a schedule for a doctor")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{doctorId}/schedules/{scheduleId}")
    public void deleteSchedule(@PathVariable @Min(1) long doctorId, @PathVariable @Min(1) Long scheduleId) {
        scheduleService.deleteSchedule(doctorId, scheduleId);
    }
}
