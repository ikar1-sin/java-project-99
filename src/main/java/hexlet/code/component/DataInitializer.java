package hexlet.code.component;

import hexlet.code.entity.User;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer {

    private UserRepository userRepository;
    private UserService userService;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void run() {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            var email = "hexlet@example.com";
            var user = new User();
            user.setEmail(email);
            user.setPasswordDigest(passwordEncoder.encode("qwerty"));
            userRepository.save(user);
        }

    }

}
