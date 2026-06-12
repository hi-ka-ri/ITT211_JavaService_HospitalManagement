package ra.demo.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalRecordRequest {
    @NotNull(message = "Mã lịch hẹn không được để trống")
    private Long appointmentId;

    @NotBlank(message = "Chẩn đoán không được để trống")
    @Size(max = 1000, message = "Chẩn đoán không được vượt quá 1000 ký tự")
    private String diagnosis;
}
