package ra.demo.service;

import org.junit.jupiter.api.Test;
import ra.demo.model.dto.response.MedicalRecordResponse;
import ra.demo.model.entity.Appointment;
import ra.demo.model.entity.MedicalRecord;
import ra.demo.model.entity.User;
import ra.demo.model.enums.RoleName;
import ra.demo.repository.AppointmentRepository;
import ra.demo.repository.MedicalRecordRepository;
import ra.demo.repository.UserRepository;
import ra.demo.service.CloudStorageService;
import ra.demo.service.impl.MedicalRecordServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MedicalRecordServiceFindByIdTest {
    private final MedicalRecordRepository medicalRecordRepository = mock(MedicalRecordRepository.class);
    private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CloudStorageService cloudStorageService = mock(CloudStorageService.class);
    private final MedicalRecordServiceImpl medicalRecordService =
            new MedicalRecordServiceImpl(medicalRecordRepository, appointmentRepository, userRepository, cloudStorageService);

    @Test
    void findById_success() {
        User patient = User.builder().id(1L).fullName("Patient One").role(RoleName.PATIENT).build();
        User doctor = User.builder().id(2L).fullName("Doctor One").role(RoleName.DOCTOR).build();
        Appointment appointment = Appointment.builder().id(10L).patient(patient).doctor(doctor).build();

        MedicalRecord record = MedicalRecord.builder()
                .id(1L)
                .appointment(appointment)
                .patient(patient)
                .doctor(doctor)
                .diagnosis("Normal")
                .fileUrl("record.pdf")
                .createdAt(LocalDateTime.now())
                .build();

        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        MedicalRecordResponse response = medicalRecordService.findById(1L);

        assertEquals("Normal", response.getDiagnosis());
        assertEquals("record.pdf", response.getFileUrl());
    }
}
