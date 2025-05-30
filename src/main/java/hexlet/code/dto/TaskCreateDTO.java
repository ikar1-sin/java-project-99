package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.entity.TaskStatus;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskCreateDTO {
    private int index;
    private String name;
    private String description;
    private TaskStatus status;
    @JsonProperty(namespace = "assignee_id")
    private JsonNullable<Long> assigneeId;
}
