package ra.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import ra.demo.model.dto.response.UserResponse;
import ra.demo.model.entity.User;
import ra.demo.model.enums.RoleName;
import ra.demo.repository.UserRepository;
import ra.demo.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceFindByIdTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

    @Test
    void findById_success() {
        User user = User.builder()
                .id(1L)
                .username("patient01")
                .email("patient@gmail.com")
                .fullName("Patient One")
                .password("123456")
                .role(RoleName.PATIENT)
                .active(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.findById(1L);

        assertEquals("patient01", response.getUsername());
        assertEquals("Patient One", response.getFullName());
    }
}
