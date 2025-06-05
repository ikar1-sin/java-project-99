package hexlet.code;

import net.datafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AppApplication {

    @Bean
    public Faker getFaker() {
        return new Faker();
    }

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
