package ra.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import ra.demo.model.dto.request.LoginRequest;
import ra.demo.model.dto.request.RegisterRequest;
import ra.demo.model.dto.response.JwtResponse;
import ra.demo.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AuthControllerTest {
    private final AuthService authService = mock(AuthService.class);
    private final MockMvc mockMvc = standaloneSetup(new AuthController(authService)).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("patient01");
        request.setPassword("123456");

        JwtResponse response = JwtResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }
}
