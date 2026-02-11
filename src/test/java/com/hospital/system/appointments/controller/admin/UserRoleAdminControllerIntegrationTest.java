package com.hospital.system.appointments.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.enums.Role;
import com.hospital.system.appointments.repository.DoctorRepository;
import com.hospital.system.appointments.request.DoctorRequest;
import com.hospital.system.appointments.util.UserTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserRoleAdminControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private DoctorRepository doctorRepository;
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
    }

    @Test
    void promoteUserToAdmin_returns200() throws Exception{
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);
        User user2 = userTestUtil.createUser(2);

        mockMvc.perform(put("/api/admin/users/"+user2.getId()+"/promote-to-admin").with(authentication(auth)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorities.length()").value(user2.getAuthorities().size()))
                .andExpect(jsonPath("$.authorities[*].authority").value(hasItem(Role.USER.getAuthority())))
                .andExpect(jsonPath("$.authorities[*].authority").value(hasItem(Role.ADMIN.getAuthority())));

    }

    @Test
    void promoteUserToDoctor_returns201() throws Exception{
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        User user2 = userTestUtil.createUser(2);
        DoctorRequest doctorRequest = createDoctorRequest();

        mockMvc.perform(post("/api/admin/users/"+user2.getId()+"/promote-to-doctor")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userId").value(user2.getId()))
                .andExpect(jsonPath("$.email").value(user2.getEmail()))
                .andExpect(jsonPath("$.fullName").value(doctorRequest.getFullName()))
                .andExpect(jsonPath("$.specialisation").value(doctorRequest.getSpecialisation()))
                .andExpect(jsonPath("$.roomNumber").value(doctorRequest.getRoomNumber()))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.authorities[*].authority").value(hasItem(Role.USER.getAuthority())))
                .andExpect(jsonPath("$.authorities[*].authority").value(hasItem(Role.DOCTOR.getAuthority())));

        assertTrue(doctorRepository.findByUserId(user2.getId()).isPresent(), "Doctor not found in Db");

    }

    @Test
    void promoteNonExistentUserToAdmin_return404() throws Exception{
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        mockMvc.perform(put("/api/admin/users/"+999+"/promote-to-admin")
                        .with(authentication(auth)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void promoteNonExistentUserToDoctor_return404() throws Exception{
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        DoctorRequest doctorRequest = createDoctorRequest();

        mockMvc.perform(post("/api/admin/users/"+999+"/promote-to-doctor")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void promoteAlreadyAdminToAdmin_return403() throws Exception{
        User admin1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(admin1);

        User admin2 = userTestUtil.createAdmin(1);

        mockMvc.perform(put("/api/admin/users/"+admin2.getId()+"/promote-to-admin")
                        .with(authentication(auth)))
                .andExpect(status().isForbidden());

    }

    @Test
    void promoteToDoctor_withInvalidBody_return400() throws Exception{
        User adminUser1 = userTestUtil.createAdmin(1);
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser1);

        User user2 = userTestUtil.createUser(2);
        DoctorRequest doctorRequest = createDoctorRequest();
        doctorRequest.setSpecialisation("");

        mockMvc.perform(post("/api/admin/users/"+user2.getId()+"/promote-to-doctor")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isBadRequest());
    }


    private DoctorRequest createDoctorRequest(){
        String fullName = "Dr Adam Smith";
        String specialisation = "General";
        String roomNumber = "101A";

        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setFullName(fullName);
        doctorRequest.setSpecialisation(specialisation);
        doctorRequest.setRoomNumber(roomNumber);

        return doctorRequest;
    }

    
}
