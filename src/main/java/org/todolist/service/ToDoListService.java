package org.todolist.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.todolist.repository.dao.TaskDao;
import org.todolist.repository.entity.Priority;
import org.todolist.repository.entity.Task;
import org.todolist.repository.entity.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ToDoListService {

    private final TaskDao taskDao;

    public ToDoListService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    // Create Task
    public Task createTask(Task task) {
        return taskDao.save(task);
    }

    // View All Tasks
    public List<Task> getAllTasks() {
        return taskDao.findAll();
    }

    // View Task by ID
    public Optional<Task> getTaskById(String id) {
        return taskDao.findById(id);
    }

    // Update Task
    public Task updateTask(String id, Task updates) {
        Task existing = taskDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));

        if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());
        if (updates.getPriority() != null) existing.setPriority(updates.getPriority());
        if (updates.getDueDate() != null) existing.setDueDate(updates.getDueDate());

        return taskDao.save(existing);
    }

    // Delete Task
    public void deleteTask(String id) {
        taskDao.deleteById(id);
    }

    // Mark task as COMPLETED
    public Task markComplete(String id) {
        Task task = taskDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));
        task.setStatus(TaskStatus.COMPLETED);
        return taskDao.save(task);
    }

    // Mark task as PENDING
    public Task markPending(String id) {
        Task task = taskDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));
        task.setStatus(TaskStatus.PENDING);
        return taskDao.save(task);
    }

    /**
     * Flexible search across title (substring), status, priority, due date range and sorting.
     */
    public List<Task> searchTasks(String title,
                                  TaskStatus status,
                                  Priority priority,
                                  LocalDate dueFrom,
                                  LocalDate dueTo,
                                  String sortBy,
                                  Sort.Direction direction) {
        return taskDao.search(title, status, priority, dueFrom, dueTo, sortBy, direction);
    }

    /**
     * Filter endpoint: filter by status, priority, single dueDate and sort.
     * If dueDate provided, searches tasks with dueDate equal to that date.
     */
    public List<Task> filterTasks(TaskStatus status,
                                  Priority priority,
                                  LocalDate dueDate,
                                  String sortBy,
                                  Sort.Direction direction) {
        LocalDate from = null;
        LocalDate to = null;
        if (dueDate != null) {
            from = dueDate;
            to = dueDate;
        }
        return taskDao.search(null, status, priority, from, to, sortBy, direction);
    }
}
