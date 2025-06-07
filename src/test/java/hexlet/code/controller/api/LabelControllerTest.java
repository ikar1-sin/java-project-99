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
import hexlet.code.entity.Label;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityGenerator entityGenerator;

    @Autowired
    private LabelMapper labelMapper;

    private Label label;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void set() {
        labelRepository.deleteAll();
        label = Instancio.of(entityGenerator.getLabelModel()).create();
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @AfterEach
    public void clear() {
        labelRepository.deleteAll();
    }

    @Test
    public void indexLabelTest() throws Exception {
        var result = mockMvc.perform(get("/api/labels")
                .with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var data = labelRepository.findAll().stream()
                        .map(labelMapper::map)
                                .toList();

        assertThatJson(body).isArray();
        assertThatJson(body).isEqualTo(data);
    }

    @Test
    public void showLabelTest() throws Exception {
        labelRepository.save(label);
        var result = mockMvc.perform(get("/api/labels/{id}", label.getId())
                .with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                l -> l.node("name").isEqualTo(label.getName())
        );
    }

    @Test
    public void createLabelTest() throws Exception {
        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(label));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var data = labelRepository.findByName(label.getName()).orElse(null);

        assertThat(data).isNotNull();
        assertThat(data.getName()).isEqualTo(label.getName());
    }

    @Test
    public void updateLabelTest() throws Exception {
        labelRepository.save(label);

        Map<String, String> data = new HashMap<>();
        data.put("name", "bug");

        var request = put("/api/labels/{id}", label.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var body = labelRepository.findById(label.getId()).orElse(null);

        assertThat(body).isNotNull();
        assertThat(body.getName()).isEqualTo("bug");
    }

    @Test
    public void deleteLabelTest() throws Exception {
        labelRepository.save(label);

        mockMvc.perform(delete("/api/labels/{id}", label.getId())
                .with(token));
        var body = labelRepository.findById(label.getId()).orElse(null);
        assertThat(body).isNull();
    }

}
