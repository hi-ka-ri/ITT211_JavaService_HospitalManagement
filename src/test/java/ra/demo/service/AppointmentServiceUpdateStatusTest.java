package ra.demo.service;

import org.junit.jupiter.api.Test;
import ra.demo.model.dto.request.AppointmentStatusRequest;
import ra.demo.model.dto.response.AppointmentResponse;
import ra.demo.model.entity.Appointment;
import ra.demo.model.entity.User;
import ra.demo.model.enums.AppointmentStatus;
import ra.demo.model.enums.RoleName;
import ra.demo.repository.AppointmentRepository;
import ra.demo.repository.UserRepository;
import ra.demo.service.impl.AppointmentServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppointmentServiceUpdateStatusTest {
    private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final AppointmentServiceImpl appointmentService =
            new AppointmentServiceImpl(appointmentRepository, userRepository);

    @Test
    void updateStatus_success() {
        User patient = User.builder().id(1L).fullName("Patient One").role(RoleName.PATIENT).build();
        User doctor = User.builder().id(2L).fullName("Doctor One").role(RoleName.DOCTOR).build();

        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .appointmentTime(LocalDateTime.now().plusDays(1))
                .status(AppointmentStatus.PENDING)
                .symptomDescription("Fever")
                .build();

        AppointmentStatusRequest request = new AppointmentStatusRequest();
        request.setStatus(AppointmentStatus.APPROVED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentResponse response = appointmentService.updateStatus(1L, request);

        assertEquals(AppointmentStatus.APPROVED, response.getStatus());
    }
}
