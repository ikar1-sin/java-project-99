package hexlet.code.repository;

import hexlet.code.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    TaskStatus findBySlug(String slug);
}
