package ra.demo.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ra.demo.model.enums.RoleName;

@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50,
            message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_.-]+$",
            message = "Tên đăng nhập chỉ được chứa chữ, số, dấu gạch dưới, dấu chấm hoặc dấu gạch ngang")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100,
            message = "Email không được vượt quá 100 ký tự")
    private String email;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100,
            message = "Họ và tên phải từ 2 đến 100 ký tự")
    private String fullName;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 50,
            message = "Mật khẩu phải từ 6 đến 50 ký tự")
    private String password;

    @NotNull(message = "Vai trò không được để trống")
    private RoleName role;

    private Boolean active = true;
}
