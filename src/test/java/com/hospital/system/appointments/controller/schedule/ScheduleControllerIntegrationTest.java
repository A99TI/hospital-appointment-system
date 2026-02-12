package com.hospital.system.appointments.controller.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.system.appointments.enums.DayOfWeek;
import com.hospital.system.appointments.request.ScheduleRequest;
import com.hospital.system.appointments.support.MvcEndpointTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.util.stream.Stream;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ScheduleControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MvcEndpointTestSupport mvcEndpointTestSupport;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        objectMapper = new ObjectMapper();
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



}
