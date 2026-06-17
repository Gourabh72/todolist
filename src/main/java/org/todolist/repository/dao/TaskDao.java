package org.todolist.repository.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.todolist.repository.TaskRepository;
import org.todolist.repository.entity.Priority;
import org.todolist.repository.entity.Task;
import org.todolist.repository.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public class TaskDao {

    private final TaskRepository repository;
    private final MongoTemplate mongoTemplate;

    public TaskDao(TaskRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public Task save(Task task) {
        if (task.getCreatedDate() == null) {
            task.setCreatedDate(Instant.now());
        }
        return repository.save(task);
    }

    public Optional<Task> findById(String id) {
        return repository.findById(id);
    }

    public List<Task> findAll() {
        return repository.findAll();
    }

    public List<Task> findByStatus(TaskStatus status) {
        return repository.findByStatus(status);
    }

    public List<Task> findByPriority(Priority priority) {
        return repository.findByPriority(priority);
    }

    public List<Task> searchByTitle(String term) {
        return repository.findByTitleContainingIgnoreCase(term);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public void deleteAllById(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // Use the repository method with correct name
        repository.deleteAllById(ids);
    }

    public List<Task> search(String title,
                             TaskStatus status,
                             Priority priority,
                             LocalDate dueFrom,
                             LocalDate dueTo,
                             String sortBy,
                             Sort.Direction direction) {

        List<Criteria> criteriaList = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            Pattern regex = Pattern.compile(Pattern.quote(title), Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("title").regex(regex));
        }

        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        if (priority != null) {
            criteriaList.add(Criteria.where("priority").is(priority));
        }

        if (dueFrom != null) {
            criteriaList.add(Criteria.where("dueDate").gte(dueFrom));
        }

        if (dueTo != null) {
            criteriaList.add(Criteria.where("dueDate").lte(dueTo));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        String mappedSortField = mapSortField(sortBy);
        Sort.Direction dir = direction != null ? direction : Sort.Direction.ASC;
        query.with(Sort.by(dir, mappedSortField));

        return mongoTemplate.find(query, Task.class);
    }

    private String mapSortField(String sortBy) {
        if (sortBy == null) return "createdDate";
        switch (sortBy) {
            case "dueDate":
                return "dueDate";
            case "priority":
                return "priority";
            case "createdDate":
            default:
                return "createdDate";
        }
    }
}
