package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.entity.TaskStatus;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {
    private JsonNullable<String> name;
    private JsonNullable<String> description;
    private JsonNullable<TaskStatus> status;
    @JsonProperty(namespace = "assignee_id")
    private JsonNullable<Long> assigneeId;
}
