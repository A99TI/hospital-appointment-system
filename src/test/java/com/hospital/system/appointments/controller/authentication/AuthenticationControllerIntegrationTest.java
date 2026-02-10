package com.hospital.system.appointments.controller.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.system.appointments.request.AuthenticationRequest;
import com.hospital.system.appointments.request.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_withValidRequest_returns201() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "johndoe@email.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void login_afterRegistration_returns200WithToken() throws Exception {
        registerDefaultUser();

        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setEmail("johndoe@email.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_withIncorrectPassword_returns401() throws Exception {
        registerDefaultUser();

        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setEmail("johndoe@email.com");
        loginRequest.setPassword("password1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void login_withNonExistentEmail_returns401() throws Exception {
        registerDefaultUser();

        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setEmail("user123@email.com");
        loginRequest.setPassword("password1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void register_withDuplicateEmail_returns409() throws Exception {
        registerDefaultUser();

        RegisterRequest duplicateRegisterRequest = new RegisterRequest(
                "johndoe@email.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRegisterRequest)))
                .andExpect(status().isConflict());

    }

    @Test
    void register_withInvalidEmailFormat_returns400() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "johndoe.email.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

    }


    @Test
    void register_withMalformedEmail_returns400() throws Exception {
        registerDefaultUser();

        RegisterRequest duplicateRegisterRequest = new RegisterRequest(
                "johndoe.email.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRegisterRequest)))
                .andExpect(status().isBadRequest());
    }

    private void registerDefaultUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "johndoe@email.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
    }

}