package ra.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import ra.demo.model.dto.response.UserResponse;
import ra.demo.model.enums.RoleName;
import ra.demo.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AdminUserControllerTest {
    private final UserService userService = mock(UserService.class);
    private final MockMvc mockMvc = standaloneSetup(new AdminUserController(userService)).build();

    @Test
    void list_success() throws Exception {
        UserResponse user = UserResponse.builder()
                .id(1L)
                .username("doctor01")
                .email("doctor@gmail.com")
                .fullName("Doctor One")
                .role(RoleName.DOCTOR)
                .active(true)
                .build();

        when(userService.findAll(anyString(), any())).thenReturn(new PageImpl<>(List.of(user)));

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].username").value("doctor01"));
    }
}
