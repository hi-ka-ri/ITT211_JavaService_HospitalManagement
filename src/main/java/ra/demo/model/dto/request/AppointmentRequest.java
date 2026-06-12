package ra.demo.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentRequest {
    @NotNull(message = "Mã bác sĩ không được để trống")
    private Long doctorId;

    @NotNull(message = "Ngày giờ khám không được để trống")
    @Future(message = "Ngày giờ khám phải là thời điểm trong tương lai")
    private LocalDateTime appointmentTime;

    @NotBlank(message = "Mô tả triệu chứng không được để trống")
    private String symptomDescription;
}
