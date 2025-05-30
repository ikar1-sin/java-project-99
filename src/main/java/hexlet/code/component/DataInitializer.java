package hexlet.code.component;

import hexlet.code.entity.TaskStatus;
import hexlet.code.entity.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer {

    private UserRepository userRepository;
    private TaskStatusRepository statusRepository;
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeUser() {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            var email = "hexlet@example.com";
            var user = new User();
            user.setEmail(email);
            user.setPasswordDigest(passwordEncoder.encode("qwerty"));
            userRepository.save(user);
        }
    }

    @PostConstruct
    public void initializeTaskStatuses() {
        List<String> slugs = List.of("draft", "to_review", "to_be_fixed", "to_publish", "published");
        for (var slug: slugs) {
            var status = new TaskStatus();
            status.setSlug(slug);
            status.setName(slug.replaceAll("_", " "));
            statusRepository.save(status);
        }
    }

}
