package ra.demo.service;

import org.junit.jupiter.api.Test;
import ra.demo.model.dto.request.AppointmentRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AppointmentServiceCreateTest {
    private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final AppointmentServiceImpl appointmentService =
            new AppointmentServiceImpl(appointmentRepository, userRepository);

    @Test
    void create_success() {
        User patient = User.builder().id(1L).username("patient01").fullName("Patient One")
                .email("patient@gmail.com").password("123456").role(RoleName.PATIENT).active(true).build();
        User doctor = User.builder().id(2L).username("doctor01").fullName("Doctor One")
                .email("doctor@gmail.com").password("123456").role(RoleName.DOCTOR).active(true).build();

        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(2L);
        request.setAppointmentTime(LocalDateTime.now().plusDays(1));
        request.setSymptomDescription("Fever");

        when(userRepository.findByUsername("patient01")).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorAndAppointmentTimeAndStatusIn(any(), any(), any())).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setId(1L);
            return appointment;
        });

        AppointmentResponse response = appointmentService.create(request, "patient01");

        assertEquals(1L, response.getId());
        assertEquals(AppointmentStatus.PENDING, response.getStatus());
        assertEquals("Fever", response.getSymptomDescription());
    }
}
