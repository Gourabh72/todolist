package org.todolist.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.todolist.repository.entity.Priority;
import org.todolist.repository.entity.Task;
import org.todolist.repository.entity.TaskStatus;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(Priority priority);

    List<Task> findByTitleContainingIgnoreCase(String title);
}

