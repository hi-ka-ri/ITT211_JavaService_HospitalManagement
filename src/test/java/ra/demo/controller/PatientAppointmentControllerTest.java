package ra.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import ra.demo.model.dto.request.AppointmentRequest;
import ra.demo.model.dto.response.AppointmentResponse;
import ra.demo.model.enums.AppointmentStatus;
import ra.demo.service.AppointmentService;
import ra.demo.service.MedicalRecordService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class PatientAppointmentControllerTest {
    private final AppointmentService appointmentService = mock(AppointmentService.class);
    private final MedicalRecordService medicalRecordService = mock(MedicalRecordService.class);
    private final MockMvc mockMvc = standaloneSetup(
            new PatientAppointmentController(appointmentService, medicalRecordService)).build();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void createAppointment_success() throws Exception {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(2L);
        request.setAppointmentTime(LocalDateTime.now().plusDays(1));
        request.setSymptomDescription("Fever");

        AppointmentResponse response = AppointmentResponse.builder()
                .id(1L)
                .doctorId(2L)
                .status(AppointmentStatus.PENDING)
                .symptomDescription("Fever")
                .build();

        when(appointmentService.create(any(AppointmentRequest.class), eq("patient01"))).thenReturn(response);

        mockMvc.perform(post("/api/v1/patient/appointments")
                        .principal(new TestingAuthenticationToken("patient01", null))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }
}
