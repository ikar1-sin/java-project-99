package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDTO {
    private JsonNullable<Integer> index;
    @NotBlank
    private JsonNullable<String> title;
    private JsonNullable<String> content;
    private JsonNullable<String> status;
    @JsonProperty(namespace = "assignee_id")
    private JsonNullable<Long> assigneeId;
    private JsonNullable<Set<Long>> taskLabelIds;
}
