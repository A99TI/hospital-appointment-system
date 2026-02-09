package com.hospital.system.appointments.controller;

import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.enums.Role;
import com.hospital.system.appointments.util.UserTestUtil;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTests {

    public static Stream<UserEndpointSpec> getAllUserEndpoints(){
        return Stream.<UserEndpointSpec>of(
                new UserEndpointSpec(HttpMethod.GET, "/api/user/info", null)
        );
    }

    public record UserEndpointSpec(HttpMethod method, String path, String requestBody){}

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserTestUtil userTestUtil;

    private MockMvc mockMvc;

    private void performUserEndpointAndExpect(
            UserEndpointSpec spec, UsernamePasswordAuthenticationToken auth, int expectedStatus) throws Exception{
        MockHttpServletRequestBuilder request = buildRequest(spec);
        if (auth != null) {
            request = request.with(authentication(auth));
        }
        mockMvc.perform(request).andExpect(status().is(expectedStatus));
    }

    private MockHttpServletRequestBuilder buildRequest(UserEndpointSpec spec) {
        if (spec.method() == HttpMethod.GET) {
            return get(spec.path());
        }
        if (spec.method() == HttpMethod.DELETE) {
            return delete(spec.path());
        }
        if (spec.method() == HttpMethod.PUT) {
            MockHttpServletRequestBuilder putRequest = put(spec.path()).contentType(MediaType.APPLICATION_JSON);
            return spec.requestBody() != null ? putRequest.content(spec.requestBody()) : putRequest;
        }
        throw new IllegalStateException("Unexpected method: " + spec.method());
    }

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @ParameterizedTest
    @MethodSource("getAllUserEndpoints")
    void WhenUnauthenticated_ShouldReturn401(UserEndpointSpec endpoint) throws Exception {
        performUserEndpointAndExpect(endpoint, null, 401);
    }

    @Test
    void getUserInfo_ShouldReturnUserInfo() throws Exception{
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        mockMvc.perform(get("/api/users/info").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUser1.getId()))
                .andExpect(jsonPath("$.email").value(adminUser1.getEmail()))
                .andExpect(jsonPath("$.authorities.length()").value(adminUser1.getAuthorities().size()))
                .andExpect(jsonPath("$.authorities[*].authority").value(hasItem(Role.USER.getAuthority())))
                .andExpect(jsonPath("$.authorities[*].authority").value(hasItem(Role.ADMIN.getAuthority())));
    }

}
