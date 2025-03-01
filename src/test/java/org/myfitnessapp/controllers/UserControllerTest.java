package org.myfitnessapp.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myfitnessapp.dao.JdbcUserDao;
import org.myfitnessapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JdbcUserDao userDao;

    User adminUser;

    User normalUser;

    //TODO setup TestData class

    @BeforeEach
    void setUp() {
        when(userDao.hashPassword("admin")).thenReturn("$2a$12$6jAmhPS48Dif.iGoXDxFq.78ItfS70rOWBMByP/yq73OOzkc5BV2O");

        adminUser = User.builder()
            .id(1)
            .email("adminUser@test.com")
            .username("admin")
            .role("ADMIN")
            .build();

        adminUser.setPassword(userDao.hashPassword("admin"));

        when(userDao.hashPassword("user")).thenReturn("$2a$06$jVuP3btMsagqpMJzI.ww/O05YO6jxYXzVfOSINfXGFckX8ZTbVfQy");

        normalUser = User.builder()
            .id(2)
            .email("user@test.com")
            .username("user")
            .role("USER")
            .build();

        normalUser.setPassword(userDao.hashPassword("user"));
    }


    @Test
    void testGetUserById() throws Exception {
        long id = 1;
        when(userDao.get(id)).thenReturn(adminUser);

        mockMvc.perform(get("/api/users/" + id)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("adminUser@test.com"))
            .andExpect(jsonPath("$.username").value("admin"))
            .andExpect(jsonPath("$.password").value("$2a$12$6jAmhPS48Dif.iGoXDxFq.78ItfS70rOWBMByP/yq73OOzkc5BV2O"))
            .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userDao, times(1)).get(id);
    }

    @Test
    void getAllUsers() throws Exception {
        when(userDao.getAllUsers()).thenReturn(List.of(adminUser, normalUser));

        mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        verify(userDao, times(1)).getAllUsers();
    }

    @Test
    void verifyUser() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void partialUpdate() {
    }

    @Test
    void delete() {
    }
}