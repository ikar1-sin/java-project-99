package hexlet.code.dto;

import hexlet.code.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    private int index;
    private String name;
    private String description;
    @NotNull
    private TaskStatus status;
    private Long assigneeId;
}
