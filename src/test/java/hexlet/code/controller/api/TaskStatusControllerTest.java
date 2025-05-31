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
import hexlet.code.entity.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.EntityGenerator;
import net.datafaker.Faker;
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

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityGenerator entityGenerator;

    private TaskStatus status;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void beforeEach() {
        statusRepository.deleteAll();
        status = Instancio.of(entityGenerator.getTaskStatusModel()).create();
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @AfterEach
    public void clear() {
        statusRepository.deleteAll();
    }

    @Test
    public void indexStatusTest() throws Exception {
        statusRepository.save(status);
        var result = mockMvc.perform(get("/api/task_statuses")
                        .with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void showStatusTest() throws Exception {
        statusRepository.save(status);
        var result = mockMvc.perform(get("/api/task_statuses/{id}", status.getId())
                        .with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                s -> s.node("name").isEqualTo(status.getName()),
                s -> s.node("slug").isEqualTo(status.getSlug())
        );
    }

    @Test
    public void createStatusTest() throws Exception {

        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(status));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var body = statusRepository.findBySlug(status.getSlug());

        assertThat(body).isNotNull();
        assertThat(body.getName()).isEqualTo(status.getName());
        assertThat(body.getSlug()).isEqualTo(status.getSlug());
    }

    @Test
    public void updateStatusTest() throws Exception {
        statusRepository.save(status);

        Map<String, String> data = new HashMap<>();
        data.put("name", "megaDraft");

        var request = put("/api/task_statuses/{id}", status.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var body = statusRepository.findById(status.getId()).get();

        assertThat(body.getName()).isEqualTo("megaDraft");
    }

    @Test
    public void deleteStatusTest() throws Exception {
        statusRepository.save(status);

        mockMvc.perform(delete("/api/task_statuses/{id}", status.getId()).with(token))
                .andExpect(status().isNoContent());

        var result = statusRepository.findById(status.getId()).orElse(null);
        assertThat(result).isNull();
    }

}
