package com.hospital.system.appointments.controller.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.system.appointments.entity.Doctor;
import com.hospital.system.appointments.enums.DayOfWeek;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ScheduleControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
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

        ScheduleRequest scheduleRequest = createDefaultSchedule();

        mockMvc.perform(post("/api/doctors/"+doctor1.getId()+"/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleRequest))
                .with(authentication(auth)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.doctorId").value(doctor1.getId()))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.startTime").value("09:30:00"))
                .andExpect(jsonPath("$.endTime").value("17:30:00"))
                .andExpect(jsonPath("$.maxPatients").value(30));

    }

    private ScheduleRequest createDefaultSchedule() {
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        scheduleRequest.setDayOfWeek(DayOfWeek.MONDAY);
        scheduleRequest.setStartTime(LocalTime.of(9, 30));
        scheduleRequest.setEndTime(LocalTime.of(17, 30));
        scheduleRequest.setMaxPatients(30);
        return scheduleRequest;
    }

}
