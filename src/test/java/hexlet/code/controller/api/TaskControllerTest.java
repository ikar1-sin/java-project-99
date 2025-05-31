package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.entity.Task;
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

import java.util.HashMap;
import java.util.Map;

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
    private ObjectMapper om;

    @Autowired
    private EntityGenerator entityGenerator;

    private Task task;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void set() {
        taskRepository.deleteAll();
        statusRepository.deleteAll();
        userRepository.deleteAll();
        var user = Instancio.of(entityGenerator.getUserModel()).create();
        userRepository.save(user);
        var taskStatus = Instancio.of(entityGenerator.getTaskStatusModel()).create();
        statusRepository.save(taskStatus);
        task = Instancio.of(entityGenerator.getTaskModel()).create();
        task.setAssignee(user);
        task.setTaskStatus(taskStatus);
        token = jwt().jwt(builder -> builder.subject(user.getEmail()));
    }

    @AfterEach
    public void clear() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        statusRepository.deleteAll();
    }

    @Test
    public void indexTaskTest() throws Exception {
        var result = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
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
                t -> t.node("name").isEqualTo(task.getName()),
                t -> t.node("description").isEqualTo(task.getDescription())
        );
    }

    @Test
    public void createTaskTest() throws Exception {
        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(task));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var data = taskRepository.findById(task.getId()).get();

        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo(task.getName());
        assertThat(data.getDescription()).isEqualTo(task.getDescription());
    }

    @Test
    public void updateTaskTest() throws Exception {
        taskRepository.save(task);

        Map<String, String> data = new HashMap<>();
        data.put("name", "Task 1");

        var request = put("/api/tasks/{id}", task.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());

        var body = taskRepository.findById(task.getId()).get();

        assertThat(body.getName()).isEqualTo(data.get("name"));
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
