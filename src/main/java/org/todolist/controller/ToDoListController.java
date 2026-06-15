package org.todolist.controller;

import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.todolist.repository.entity.Priority;
import org.todolist.repository.entity.Task;
import org.todolist.repository.entity.TaskStatus;
import org.todolist.service.ToDoListService;


import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1.0")
public class ToDoListController {

    private final ToDoListService service;

    public ToDoListController(ToDoListService service) {
        this.service = service;
    }

    // Create Task
    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task created = service.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Get All Tasks
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(service.getAllTasks());
    }

    // Get Task by ID
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        return service.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update Task
    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task updates) {
        try {
            Task updated = service.updateTask(id, updates);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete Task
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // Mark Complete
    @PatchMapping("/tasks/{id}/complete")
    public ResponseEntity<Task> markComplete(@PathVariable String id) {
        try {
            Task updated = service.markComplete(id);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Mark Pending
    @PatchMapping("/tasks/{id}/pending")
    public ResponseEntity<Task> markPending(@PathVariable String id) {
        try {
            Task updated = service.markPending(id);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Search Tasks
    @GetMapping("/tasks/search")
    public ResponseEntity<List<Task>> searchTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo,
            @RequestParam(required = false, defaultValue = "createdDate") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction direction
    ) {
        List<Task> result = service.searchTasks(title, status, priority, dueFrom, dueTo, sortBy, direction);
        return ResponseEntity.ok(result);
    }

    // Filter Tasks
    @GetMapping("/tasks/filter")
    public ResponseEntity<List<Task>> filterTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false, defaultValue = "createdDate") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction direction
    ) {
        List<Task> result = service.filterTasks(status, priority, dueDate, sortBy, direction);
        return ResponseEntity.ok(result);
    }
}
