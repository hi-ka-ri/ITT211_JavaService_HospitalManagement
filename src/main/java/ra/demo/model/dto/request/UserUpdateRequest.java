package ra.demo.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ra.demo.model.enums.RoleName;

public class UserUpdateRequest {
    @NotBlank private String fullName;
    @Email private String email;
    private String phone;
    private String address;
    private Boolean active;
    private RoleName role;
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public RoleName getRole() { return role; }
    public void setRole(RoleName role) { this.role = role; }
}
