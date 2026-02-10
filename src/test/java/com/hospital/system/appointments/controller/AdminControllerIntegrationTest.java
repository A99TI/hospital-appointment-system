package com.hospital.system.appointments.controller;

import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.repository.UserRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AdminControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserTestUtil userTestUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MvcEndpointTestSupport mvcEndpointTestSupport;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    public static Stream<MvcEndpointTestSupport.EndpointSpec> getAllAdminEndpoints() {
        return  Stream.<MvcEndpointTestSupport.EndpointSpec>of(
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.GET, "/api/admin/users", null),
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.DELETE, "/api/admin/users/1", null),
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.PUT, "/api/admin/users/1/promote-to-admin", null),
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.PUT, "/api/admin/users/1/promote-to-doctor",
                        "{\"fullName\":\"Dr A\",\"specialisation\":\"GP\",\"roomNumber\":\"1\",\"active\":true}")
        );
    }

    @ParameterizedTest
    @MethodSource("getAllAdminEndpoints")
    void adminEndpoints_whenUnauthenticated_returns401(MvcEndpointTestSupport.EndpointSpec endpoint) throws Exception {
        mvcEndpointTestSupport.performEndpointAndExpect(mockMvc, endpoint, null, 401);
    }

    @ParameterizedTest
    @MethodSource("getAllAdminEndpoints")
    void adminEndpoints_whenAuthenticatedAsNonAdmin_returns403(MvcEndpointTestSupport.EndpointSpec endpoint) throws Exception {
        User regularUser = userTestUtil.createUser(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(regularUser);
        mvcEndpointTestSupport.performEndpointAndExpect(mockMvc, endpoint, auth, 403);
    }

    @Test
    void getAllUsers_whenAuthenticatedAsAdmin_returns200WithAllUsers() throws Exception {
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        User user2 = userTestUtil.createUser(2);
        User user3 = userTestUtil.createUser(3);

        mockMvc.perform(get("/api/admin/users").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[?(@.email == '" + adminUser1.getEmail() + "')]").exists())
                .andExpect(jsonPath("$[?(@.email == '" + user2.getEmail() + "')]").exists())
                .andExpect(jsonPath("$[?(@.email == '" + user3.getEmail() + "')]").exists());

    }

    @Test
    void deleteUser_whenTargetIsNonAdmin_returns204AndDeletesUser() throws Exception {
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        User user2 = userTestUtil.createUser(2);


        mockMvc.perform(delete("/api/admin/users/" + user2.getId()).with(authentication(auth)))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.findById(user2.getId()).isPresent(), "Created user should not exist after deletion");

    }

    @Test
    void deleteUser_whenTargetIsAdmin_returns403() throws Exception {
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        mockMvc.perform(delete("/api/admin/users/" + adminUser1.getId()).with(authentication(auth)))
                .andExpect(status().isForbidden());

    }

    @Test
    void deleteUser_whenUserIdNotFound_returns404() throws Exception {
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        mockMvc.perform(delete("/api/admin/users/9999").with(authentication(auth)))
                .andExpect(status().isNotFound());

    }

    @Test
    void deleteUser_whenUserIdNegative_returns400() throws Exception {
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        mockMvc.perform(delete("/api/admin/users/-1").with(authentication(auth)))
                .andExpect(status().isBadRequest());

    }

}



