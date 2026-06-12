package ra.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.demo.exception.ApiException;
import ra.demo.exception.ConflictException;
import ra.demo.mapper.ResponseMapper;
import ra.demo.model.dto.request.UserRequest;
import ra.demo.model.dto.response.UserResponse;
import ra.demo.model.entity.User;
import ra.demo.repository.UserRepository;
import ra.demo.service.UserService;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public Page<UserResponse> findAll(String keyword, Pageable p) {
        String k = keyword == null ? "" : keyword;
        Page<User> page = repo.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(k, k, p);
        var content = page.getContent().stream().map(ResponseMapper::toUser).collect(Collectors.toList());
        return new PageImpl<>(content, p, page.getTotalElements());
    }

    public UserResponse findById(Long id) {
        return ResponseMapper.toUser(getEntity(id));
    }

    private User getEntity(Long id) {
        return repo.findById(id).orElseThrow(() -> new ApiException("Không tìm thấy người dùng"));
    }

    @Transactional
    public UserResponse create(UserRequest r) {
        String username = r.getUsername().trim();
        String email = r.getEmail().trim();
        if (repo.existsByUsername(username) || repo.existsByEmail(email))
            throw new ConflictException("Username hoặc email này đã được sử dụng");
        User saved = repo.save(User.builder().username(username).email(email).fullName(r.getFullName().trim())
                .password(encoder.encode(r.getPassword())).role(r.getRole())
                .active(r.getActive() == null || r.getActive()).build());
        return ResponseMapper.toUser(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest r) {
        User u = getEntity(id);
        String email = r.getEmail().trim();
        if (!u.getEmail().equals(email) && repo.existsByEmail(email))
            throw new ConflictException("Email này đã được sử dụng");
        u.setEmail(email);
        u.setFullName(r.getFullName().trim());
        u.setRole(r.getRole());
        u.setActive(r.getActive() == null || r.getActive());
        if (r.getPassword() != null && !r.getPassword().isBlank()) u.setPassword(encoder.encode(r.getPassword()));
        return ResponseMapper.toUser(repo.save(u));
    }

    @Transactional
    public void deactivate(Long id) {
        User u = getEntity(id);
        u.setActive(false);
        repo.save(u);
    }
}
