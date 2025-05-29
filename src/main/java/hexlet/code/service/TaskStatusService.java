package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private TaskStatusMapper statusMapper;

    public List<TaskStatusDTO> index() {
        var statuses = statusRepository.findAll();
        return statuses.stream()
                .map(s -> statusMapper.map(s))
                .toList();
    }

    public TaskStatusDTO show(Long id) {
        var status = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task Status with id " + id + " not found"));
        return statusMapper.map(status);
    }

    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        var status = statusMapper.map(dto);
        statusRepository.save(status);
        return statusMapper.map(status);
    }

    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto) {
        var status = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task Status with id " + id + " not found"));
        statusMapper.update(status, dto);
        statusRepository.save(status);
        return statusMapper.map(status);
    }

    public void delete(Long id) {
           statusRepository.deleteById(id);
    }

}
