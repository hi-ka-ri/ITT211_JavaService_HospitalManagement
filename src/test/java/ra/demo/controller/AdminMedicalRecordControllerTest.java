package ra.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import ra.demo.model.dto.response.MedicalRecordResponse;
import ra.demo.service.MedicalRecordService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AdminMedicalRecordControllerTest {
    private final MedicalRecordService medicalRecordService = mock(MedicalRecordService.class);
    private final MockMvc mockMvc = standaloneSetup(new AdminMedicalRecordController(medicalRecordService)).build();

    @Test
    void detail_success() throws Exception {
        MedicalRecordResponse response = MedicalRecordResponse.builder()
                .id(1L)
                .diagnosis("Normal")
                .fileUrl("record.pdf")
                .build();

        when(medicalRecordService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.diagnosis").value("Normal"));
    }
}
