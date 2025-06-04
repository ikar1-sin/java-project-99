package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Integer index;
    @NotBlank
    private String title;
    private String content;
    private String status;
    @JsonProperty(namespace = "assignee_id")
    private Long assigneeId;
    private Set<Long> labelIds;
}
