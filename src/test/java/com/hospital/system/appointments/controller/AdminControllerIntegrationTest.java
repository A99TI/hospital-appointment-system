package com.hospital.system.appointments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.repository.UserRepository;
import com.hospital.system.appointments.util.UserTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws  Exception{
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        User user2 = userTestUtil.createUser(2);
        User user3 = userTestUtil.createUser(3);

        mockMvc.perform(get("/api/admin/users")
                        .with(request -> {
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[?(@.email == '" + adminUser1.getEmail() + "')]").exists())
                .andExpect(jsonPath("$[?(@.email == '" + user2.getEmail() + "')]").exists())
                .andExpect(jsonPath("$[?(@.email == '" + user3.getEmail() + "')]").exists());

    }

    @Test
    void deleteUsers_ShouldDeleteStoredUser() throws  Exception{
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        User user2 = userTestUtil.createUser(2);


        mockMvc.perform(delete("/api/admin/users/" + user2.getId())
                .with(reqeust -> {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return reqeust;
                }))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.findById(user2.getId()).isPresent(), "Created user should not exist after deletion");

    }



}



