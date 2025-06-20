package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.entity.Label;
import hexlet.code.entity.Task;
import hexlet.code.entity.TaskStatus;
import hexlet.code.entity.User;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.EntityGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityGenerator entityGenerator;

    private User user;

    private Task task;

    private TaskStatus taskStatus;

    private Label label;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;
    @Autowired
    private TaskMapper taskMapper;

    @BeforeEach
    public void set() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        labelRepository.deleteAll();
        statusRepository.deleteAll();

        user = Instancio.of(entityGenerator.getUserModel()).create();
        userRepository.save(user);

        taskStatus = Instancio.of(entityGenerator.getTaskStatusModel()).create();
        statusRepository.save(taskStatus);

        label = Instancio.of(entityGenerator.getLabelModel()).create();
        Set<Label> labels = Set.of(label);
        labelRepository.save(label);

        task = Instancio.of(entityGenerator.getTaskModel()).create();
        task.setAssignee(user);
        task.setTaskStatus(taskStatus);
        task.setLabels(labels);

        token = jwt().jwt(builder -> builder.subject(user.getEmail()));
    }

    @AfterEach
    public void clear() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        labelRepository.deleteAll();
        statusRepository.deleteAll();
    }

    @Test
    public void indexTaskTest() throws Exception {
        var result = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        var data = taskRepository.findAll().stream()
                        .map(taskMapper::map)
                                .toList();

        assertThatJson(body).isArray();
        assertThatJson(body).isEqualTo(data);
    }

    @Test
    public void indexTaskWithTitleContTest() throws Exception {
        String titleCont = "Target";
        String targetTitle = "findMyTargetTitle";
        task.setName(targetTitle);
        taskRepository.save(task);
        var result = mockMvc.perform(get("/api/tasks?titleCont=" + targetTitle)
                .with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .isArray()
                .allSatisfy(element -> {
                    assertThatJson(element)
                            .and(v -> v.node("title").asString().contains(titleCont));
                });
    }

    @Test
    public void indexWithAssigneeIdTest() throws Exception {
        Long assigneeId = user.getId();
        taskRepository.save(task);

        user.setId(null);
        user.setEmail("java@example.com");
        userRepository.save(user);
        task.setAssignee(user);
        Long otherUserId = user.getId();
        taskRepository.save(task);

         var result = mockMvc.perform(get("/api/tasks?assigneeId=" + assigneeId)
                        .with(token))
                .andExpect(status().isOk())
                .andReturn();

         var body = result.getResponse().getContentAsString();

         assertThatJson(body)
                 .isArray()
                 .allSatisfy(element -> {
                     assertThatJson(element)
                             .node("assigneeId")
                             .isEqualTo(assigneeId);
                 });

         assertThatJson(body)
                 .isArray()
                 .allSatisfy(element -> {
                     assertThatJson(element)
                             .node("assigneeId")
                             .isNotEqualTo(otherUserId);
                 });
    }

    @Test
    public void indexWithStatusSlugTest() throws Exception {
        String statusSlug = "to_review";
        String targetSlug = "Find_to_review_slug";
        taskStatus.setSlug(targetSlug);
        task.setTaskStatus(taskStatus);
        statusRepository.save(taskStatus);
        taskRepository.save(task);

        var result = mockMvc.perform(get("/api/tasks?status=" + statusSlug)
                .with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body)
                .isArray()
                .allSatisfy(el -> {
                    assertThatJson(el).and(
                            v -> v.node("status").isEqualTo(targetSlug)
                    );
                });
    }

    @Test
    public void indexWithLabelTest() throws Exception {
        Long labelId = label.getId();
        taskRepository.save(task);
        label.setId(null);
        label.setName("myTestLabel");
        labelRepository.save(label);
        Long otherLabel = label.getId();
        Set<Label> labels = Set.of(label);
        task.setLabels(labels);
        taskRepository.save(task);

        var result = mockMvc.perform(get("/api/tasks?labelId=" + labelId)
                        .with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body  = result.getResponse().getContentAsString();

        assertThatJson(body)
                .isArray()
                .allSatisfy(el -> {
                    assertThatJson(el)
                            .node("labelIds")
                            .isArray()
                            .contains(BigDecimal.valueOf(labelId));
                });

        assertThatJson(body)
                .isArray()
                .allSatisfy(el -> {
                    assertThatJson(el)
                            .node("labelIds")
                            .isArray()
                            .doesNotContain(BigDecimal.valueOf(otherLabel));
                });
    }

    @Test
    public void showTaskTest() throws Exception {
        taskRepository.save(task);
        var data = mockMvc.perform(get("/api/tasks/{id}", task.getId())
                .with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = data.getResponse().getContentAsString();

        assertThatJson(body).and(
                t -> t.node("title").isEqualTo(task.getName()),
                t -> t.node("content").isEqualTo(task.getDescription())
        );
    }

    @Test
    public void createTaskTest() throws Exception {
        var dto = new TaskCreateDTO();
        dto.setTitle(task.getName());
        dto.setContent(task.getDescription());
        dto.setIndex(task.getIndex());
        dto.setAssigneeId(user.getId());
        dto.setStatus(taskStatus.getSlug());
        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var data = taskRepository.findByName(dto.getTitle()).orElse(null);

        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo(task.getName());
        assertThat(data.getDescription()).isEqualTo(task.getDescription());
        assertThat(data.getIndex()).isEqualTo(task.getIndex());
    }

    @Test
    public void updateTaskTest() throws Exception {
        taskRepository.save(task);

        Map<String, String> data = new HashMap<>();
        data.put("title", "Task 1");

        var request = put("/api/tasks/{id}", task.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());

        var body = taskRepository.findById(task.getId()).orElse(null);

        assertThat(body).isNotNull();
        assertThat(body.getName()).isEqualTo("Task 1");
    }

    @Test
    public void deleteTaskTest() throws Exception {
        taskRepository.save(task);
        mockMvc.perform(delete("/api/tasks/{id}", task.getId())
                .with(token));
        var data = taskRepository.findById(task.getId()).orElse(null);
        assertThat(data).isNull();
    }
}
