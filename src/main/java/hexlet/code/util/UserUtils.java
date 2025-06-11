package hexlet.code.util;

import hexlet.code.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    private final UserRepository userRepository;

    public UserUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isCurrentUserDeletedUser(Long id) {
        var userEmail = userRepository.findById(id).orElseThrow().getEmail();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return userEmail.equals(auth.getName());
    }

}
