package hexlet.code.repository;

import hexlet.code.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByAssigneeId(Long id);
    boolean existsByTaskStatusId(Long id);
}
