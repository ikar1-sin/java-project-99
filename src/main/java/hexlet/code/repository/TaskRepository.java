package hexlet.code.repository;

import hexlet.code.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByName(String title);
    boolean existsByAssigneeId(Long id);
    boolean existsByTaskStatusId(Long id);
}
