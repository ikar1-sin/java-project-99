package hexlet.code.dto.task_status;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusCreateDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String slug;
}
