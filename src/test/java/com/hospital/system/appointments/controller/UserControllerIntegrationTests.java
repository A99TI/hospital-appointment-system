package com.hospital.system.appointments.controller;

import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.enums.Role;
import com.hospital.system.appointments.repository.UserRepository;
import com.hospital.system.appointments.support.MvcEndpointTestSupport;
import com.hospital.system.appointments.util.UserTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTests {

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

    public static Stream<MvcEndpointTestSupport.EndpointSpec> getAllUserEndpoints(){
        return Stream.<MvcEndpointTestSupport.EndpointSpec>of(
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.GET, "/api/user/info", null),
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.DELETE, "/api/user", null),
                new MvcEndpointTestSupport.EndpointSpec(HttpMethod.PUT, "/api/user/password",
                        "{\"oldPassword\":\"test123456\",\"newPassword\":\"test123456\",\"newPasswordConfirmation\":\"test123456\"}")
        );
    }

    @ParameterizedTest
    @MethodSource("getAllUserEndpoints")
    void WhenUnauthenticated_ShouldReturn401(MvcEndpointTestSupport.EndpointSpec endpoint) throws Exception {
        mvcEndpointTestSupport.performEndpointAndExpect(mockMvc, endpoint, null, 401);
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

    @Test
    void deleteRegularUser_ShouldDeleteUser() throws Exception{

        User user1 = userTestUtil.createUser(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(user1);


        mockMvc.perform(delete("/api/users").with(authentication(auth)))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.findById(user1.getId()).isPresent(), "Created user should not exist after deletion");

    }

    @Test
    void deleteLastAdminUser_ShouldReturnForbidden() throws Exception{

        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        mockMvc.perform(delete("/api/users").with(authentication(auth)))
                .andExpect(status().isForbidden());

        assertTrue(userRepository.findById(adminUser1.getId()).isPresent(), "Created user should be present within the database");

    }
    

}
