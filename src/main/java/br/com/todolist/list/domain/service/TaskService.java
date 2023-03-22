package br.com.todolist.list.domain.service;

import br.com.todolist.list.domain.model.Task;
import br.com.todolist.list.domain.model.User;
import br.com.todolist.list.domain.repository.TaskRepository;
import br.com.todolist.list.domain.repository.UserRepository;
import br.com.todolist.list.dto.task.TaskDTO;
import br.com.todolist.list.exception.UserException;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public TaskService(TaskRepository repository, UserRepository userRepository, ModelMapper modelMapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<TaskDTO> addTask(Task data, Long userId, UriComponentsBuilder uriBuilder){
        var user = userRepository.findById(userId).orElseThrow(() -> new UserException("id " + userId + " not found!"));
        var task = modelMapper.map(data, Task.class);
        task.setUser(user);
        var uri = uriBuilder.path("todo/{id}").buildAndExpand(task.getId()).toUri();
        repository.save(task);
        return ResponseEntity.created(uri).body(modelMapper.map(task, TaskDTO.class));
    }

    public ResponseEntity<List<Task>> listTasks(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new UserException("id " + userId + " not found!");
        }
        var tasks = repository.findAllByUserIdAndCompletedIsFalse(userId);
        return ResponseEntity.ok().body(tasks);
    }
}
