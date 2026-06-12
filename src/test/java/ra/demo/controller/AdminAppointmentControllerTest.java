package ra.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import ra.demo.model.dto.request.AppointmentStatusRequest;
import ra.demo.model.dto.response.AppointmentResponse;
import ra.demo.model.enums.AppointmentStatus;
import ra.demo.service.AppointmentService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AdminAppointmentControllerTest {
    private final AppointmentService appointmentService = mock(AppointmentService.class);
    private final MockMvc mockMvc = standaloneSetup(new AdminAppointmentController(appointmentService)).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void updateStatus_success() throws Exception {
        AppointmentStatusRequest request = new AppointmentStatusRequest();
        request.setStatus(AppointmentStatus.APPROVED);

        AppointmentResponse response = AppointmentResponse.builder()
                .id(1L)
                .status(AppointmentStatus.APPROVED)
                .symptomDescription("Headache")
                .build();

        when(appointmentService.updateStatus(eq(1L), any(AppointmentStatusRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/admin/appointments/1/status")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }
}
