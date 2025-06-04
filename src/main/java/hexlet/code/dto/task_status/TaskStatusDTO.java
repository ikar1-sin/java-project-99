package hexlet.code.dto.task_status;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusDTO {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String slug;
}
