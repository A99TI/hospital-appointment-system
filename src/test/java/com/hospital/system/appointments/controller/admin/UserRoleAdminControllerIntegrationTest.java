package com.hospital.system.appointments.controller.admin;

import com.hospital.system.appointments.entity.User;
import com.hospital.system.appointments.enums.Role;
import com.hospital.system.appointments.util.UserTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
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
    private UserTestUtil userTestUtil;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
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


}
