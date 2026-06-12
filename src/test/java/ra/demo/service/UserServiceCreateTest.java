package ra.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ra.demo.model.dto.request.UserRequest;
import ra.demo.model.dto.response.UserResponse;
import ra.demo.model.entity.User;
import ra.demo.model.enums.RoleName;
import ra.demo.repository.UserRepository;
import ra.demo.service.impl.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceCreateTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

    @Test
    void create_success() {
        UserRequest request = new UserRequest();
        request.setUsername("doctor01");
        request.setEmail("doctor@gmail.com");
        request.setFullName("Doctor One");
        request.setPassword("123456");
        request.setRole(RoleName.DOCTOR);
        request.setActive(true);

        when(userRepository.existsByUsername("doctor01")).thenReturn(false);
        when(userRepository.existsByEmail("doctor@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponse response = userService.create(request);

        assertEquals(1L, response.getId());
        assertEquals("doctor01", response.getUsername());
        assertEquals(RoleName.DOCTOR, response.getRole());
    }
}
