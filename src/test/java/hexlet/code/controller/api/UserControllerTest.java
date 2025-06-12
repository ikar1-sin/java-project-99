package hexlet.code.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.entity.User;
import hexlet.code.mapper.UserMapper;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityGenerator entityGenerator;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    private User user;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void createUser() {
        user = Instancio.of(entityGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(user.getEmail()));
    }

    @AfterEach
    public void clear() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var data = userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();

        assertThatJson(body).isArray();
        assertThatJson(body).isEqualTo(data);
    }

    @Test
    public void testShow() throws Exception {
        userRepository.save(user);

        var request = mockMvc.perform(get("/api/users/{id}", user.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = request.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(user.getEmail()),
                v -> v.node("firstName").isEqualTo(user.getFirstName())
        );
    }

    @Test
    public void testCreateUser() throws Exception {
        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var data = userRepository.findByEmail(user.getEmail()).orElse(null);

        assertThat(data).isNotNull();
        assertThat(data.getEmail()).isEqualTo(user.getEmail());
        assertThat(data.getFirstName()).isEqualTo(user.getFirstName());
    }

    @Test
    public void testUpdateUser() throws Exception {
        userRepository.save(user);

        Map<String, String> data = new HashMap<>();
        data.put("firstName", "javaSpring");

        var request = put("/api/users/{id}", user.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var body = userRepository.findById(user.getId()).orElse(null);

        assertThat(body).isNotNull();
        assertThat(body.getFirstName()).isEqualTo("javaSpring");
    }

    @Test
    public void testDeleteUser() throws Exception {
        userRepository.save(user);
        var request = delete("/api/users/{id}", user.getId())
                .with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        var data = userRepository.findById(user.getId()).orElse(null);
        assertThat(data).isNull();
    }

    @Test
    public void testDeleteNotCurrentUser() throws Exception {
        userRepository.save(user);
        var oldUserId = user.getId();

        user = Instancio.of(entityGenerator.getUserModel()).create();
        userRepository.save(user);
        token = jwt().jwt(builder -> builder.subject(user.getEmail()));

        var request = delete("/api/users/" + oldUserId)
                .with(token);

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteUserWithTask() throws Exception {
        userRepository.save(user);
        var task = Instancio.of(entityGenerator.getTaskModel()).create();
        var taskStatus = Instancio.of(entityGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(taskStatus);
        var label = Instancio.of(entityGenerator.getLabelModel()).create();
        labelRepository.save(label);
        var labels = Set.of(label);
        task.setAssignee(user);
        task.setTaskStatus(taskStatus);
        task.setLabels(labels);
        taskRepository.save(task);

        var request = delete("/api/users/" + user.getId())
                .with(token);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

    }
}
