package br.com.nogueirawelton.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.nogueirawelton.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody TaskModel task, HttpServletRequest request) {
      var userId = request.getAttribute("userId");
      task.setUserId((UUID)userId);

      var currentDate = LocalDateTime.now();

      if(currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getEndAt())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The start/end date must be greater than the current date"); 
      }

      if(task.getStartAt().isAfter(task.getEndAt())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The end date must be greater than the start date"); 
      }
      
      var createdTask = this.taskRepository.save(task);
      return ResponseEntity.status(HttpStatus.OK).body(createdTask); 
    }

    @GetMapping("")
    public List<TaskModel> list(HttpServletRequest request) {
      var userId = request.getAttribute("userId");

      var tasks = this.taskRepository.findByUserId((UUID) userId);

      return tasks;
    }
  
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel task, HttpServletRequest request, @PathVariable UUID id) {
      var userId = request.getAttribute("userId");
      var taskData = this.taskRepository.findById(id).orElse(null);

      if(taskData == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }

      if(!taskData.getUserId().equals(userId)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User don't have permission to update this task");
      }

      Utils.copyNonNullProperties(task, taskData);

      var updatedTask = this.taskRepository.save(taskData);
      return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }
  }
