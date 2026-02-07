package com.hospital.system.appointments.controller;

import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.repository.UserRepository;
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

    public static java.util.stream.Stream<AdminEndpointSpec> getAllAdminEndpoints() {
        return java.util.stream.Stream.<AdminEndpointSpec>of(
                new AdminEndpointSpec(HttpMethod.GET, "/api/admin/users", null),
                new AdminEndpointSpec(HttpMethod.DELETE, "/api/admin/users/1", null),
                new AdminEndpointSpec(HttpMethod.PUT, "/api/admin/users/1/promote-to-admin", null),
                new AdminEndpointSpec(HttpMethod.PUT, "/api/admin/users/1/promote-to-doctor",
                        "{\"fullName\":\"Dr A\",\"specialisation\":\"GP\",\"roomNumber\":\"1\",\"active\":true}")
        );
    }

    public record AdminEndpointSpec(HttpMethod method, String path, String requestBody) {}

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserTestUtil userTestUtil;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private void performAdminEndpointAndExpect(
            AdminEndpointSpec spec, UsernamePasswordAuthenticationToken auth, int expectedStatus) throws Exception {
        MockHttpServletRequestBuilder request = buildRequest(spec);
        if (auth != null) {
            request = request.with(authentication(auth));
        }
        mockMvc.perform(request).andExpect(status().is(expectedStatus));
    }

    private MockHttpServletRequestBuilder buildRequest(AdminEndpointSpec spec) {
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

    @ParameterizedTest
    @MethodSource("getAllAdminEndpoints")
    void WhenUnauthenticated_ShouldReturn401(AdminEndpointSpec endpoint) throws Exception {
        performAdminEndpointAndExpect(endpoint, null, 401);
    }

    @ParameterizedTest
    @MethodSource("getAllAdminEndpoints")
    void WhenNonAdmin_ShouldReturn403(AdminEndpointSpec endpoint) throws Exception {
        User regularUser = userTestUtil.createUser(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(regularUser);
        performAdminEndpointAndExpect(endpoint, auth, 403);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws  Exception{
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
    void deleteNonAdminUsers_ShouldDeleteStoredUser() throws  Exception{
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        User user2 = userTestUtil.createUser(2);


        mockMvc.perform(delete("/api/admin/users/" + user2.getId()).with(authentication(auth)))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.findById(user2.getId()).isPresent(), "Created user should not exist after deletion");

    }

    @Test
    void deleteAdminUser_ShouldReturnConflict() throws  Exception{
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        mockMvc.perform(delete("/api/admin/users/" + adminUser1.getId()).with(authentication(auth)))
                .andExpect(status().isForbidden());

    }

}



