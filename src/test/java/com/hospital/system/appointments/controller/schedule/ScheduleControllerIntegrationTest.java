package com.hospital.system.appointments.controller.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.entity.Schedule;
import com.hospital.system.appointments.enums.DayOfWeek;
import com.hospital.system.appointments.repository.ScheduleRepository;
import com.hospital.system.appointments.request.ScheduleRequest;
import com.hospital.system.appointments.support.MvcEndpointTestSupport;
import com.hospital.system.appointments.util.UserTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ScheduleControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MvcEndpointTestSupport mvcEndpointTestSupport;
    @Autowired
    private UserTestUtil userTestUtil;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    private static final String DEFAULT_SCHEDULE_JSON =
            "{\"dayOfWeek\":\"MONDAY\",\"startTime\":\"09:30\",\"endTime\":\"17:30\",\"maxPatients\":30}";

    public static Stream<MvcEndpointTestSupport.EndpointSpec> getAllScheduleEndpoints(){
        return Stream.<MvcEndpointTestSupport.EndpointSpec>of(
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.POST, "/api/doctors/1/schedules", DEFAULT_SCHEDULE_JSON),
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.GET, "/api/doctors/1/schedules", null),
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.PUT, "/api/doctors/1/schedules/1", DEFAULT_SCHEDULE_JSON),
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.GET, "/api/doctors/me/schedules", null),
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.DELETE, "/api/doctors/1/schedules/1", null)
        );
    }

    @ParameterizedTest
    @MethodSource("getAllScheduleEndpoints")
    void protectedScheduleEndpoints_whenUnauthenticated_returns401(MvcEndpointTestSupport.EndpointSpec endpoint) throws Exception {
        mvcEndpointTestSupport.performEndpointAndExpect(mockMvc, endpoint, null, 401);
    }

    @Test
    void createSchedule_returns201() throws Exception{
        Doctor doctor1 = userTestUtil.createDoctor(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(doctor1.getUser());

        ScheduleRequest scheduleRequest = createDefaultScheduleRequest();

        mockMvc.perform(post("/api/doctors/"+doctor1.getId()+"/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleRequest))
                .with(authentication(auth)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.doctorId").value(doctor1.getId()))
                .andExpect(jsonPath("$.dayOfWeek").value(scheduleRequest.getDayOfWeek().toString()))
                .andExpect(jsonPath("$.startTime").value(scheduleRequest.getStartTime().format(DateTimeFormatter.ISO_LOCAL_TIME)))
                .andExpect(jsonPath("$.endTime").value(scheduleRequest.getEndTime().format(DateTimeFormatter.ISO_LOCAL_TIME)))
                .andExpect(jsonPath("$.maxPatients").value(scheduleRequest.getMaxPatients()));

    }

    @Test
    void getScheduleByDoctorId_returns200() throws Exception {
        Doctor doctorOne = userTestUtil.createDoctor(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(doctorOne.getUser());

        Schedule schedule =  scheduleRepository.save(createDefaultSchedule(doctorOne));

        mockMvc.perform(get("/api/doctors/{doctorId}/schedules", doctorOne.getId())
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].doctorId").value(doctorOne.getId()))
                .andExpect(jsonPath("$[0].dayOfWeek").value(schedule.getDayOfWeek().toString()))
                .andExpect(jsonPath("$[0].startTime").value(schedule.getStartTime().format(DateTimeFormatter.ISO_LOCAL_TIME)))
                .andExpect(jsonPath("$[0].endTime").value(schedule.getEndTime().format(DateTimeFormatter.ISO_LOCAL_TIME)))
                .andExpect(jsonPath("$[0].maxPatients").value(schedule.getMaxPatients()));


    }

    private ScheduleRequest createDefaultScheduleRequest() {
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        scheduleRequest.setDayOfWeek(DayOfWeek.MONDAY);
        scheduleRequest.setStartTime(LocalTime.of(9, 30,00));
        scheduleRequest.setEndTime(LocalTime.of(17, 30,00));
        scheduleRequest.setMaxPatients(30);
        return scheduleRequest;
    }

    private Schedule createDefaultSchedule(Doctor doctor) {
        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 30,00));
        schedule.setEndTime(LocalTime.of(17, 30,00));
        schedule.setMaxPatients(30);
        return schedule;
    }

}
