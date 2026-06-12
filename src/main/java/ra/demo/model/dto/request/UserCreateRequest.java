package ra.demo.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ra.demo.model.enums.RoleName;

public class UserCreateRequest {
    @NotBlank private String username;
    @NotBlank @Size(min = 6) private String password;
    @NotBlank @Email private String email;
    @NotBlank private String fullName;
    private String phone;
    private String address;
    @NotNull private RoleName role;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public RoleName getRole() { return role; }
    public void setRole(RoleName role) { this.role = role; }
}
