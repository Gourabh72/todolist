package org.todolist.repository.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.todolist.repository.DbUserRepository;
import org.todolist.repository.entity.DbUser;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public class UserDao {
    private final DbUserRepository repository;
    private final MongoTemplate mongoTemplate;

    public UserDao(DbUserRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public DbUser save(DbUser user) {
        // ensure default role if missing
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }
        return repository.save(user);
    }

    public Optional<DbUser> findById(String id) {
        return repository.findById(id);
    }

    public Optional<DbUser> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Optional<DbUser> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<DbUser> findAll() {
        return repository.findAll();
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public void deleteAllById(List<String> ids) {
        if (ids == null || ids.isEmpty()) return;
        repository.deleteAllById(ids);
    }

    /**
     * Simple search by username or email (case insensitive, partial match).
     * Optionally sort by provided field (username|email), defaults to username.
     */
    public List<DbUser> search(String term, String sortBy, Sort.Direction direction) {
        if (term == null || term.isBlank()) {
            Sort.Direction dir = direction != null ? direction : Sort.Direction.ASC;
            String field = mapSortField(sortBy);
            return mongoTemplate.find(new Query().with(Sort.by(dir, field)), DbUser.class);
        }

        Pattern regex = Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE);
        Criteria c = new Criteria().orOperator(
                Criteria.where("username").regex(regex),
                Criteria.where("email").regex(regex)
        );

        Query query = new Query(c);
        Sort.Direction dir = direction != null ? direction : Sort.Direction.ASC;
        query.with(Sort.by(dir, mapSortField(sortBy)));

        return mongoTemplate.find(query, DbUser.class);
    }

    private String mapSortField(String sortBy) {
        if (sortBy == null) return "username";
        switch (sortBy) {
            case "email":
                return "email";
            case "username":
            default:
                return "username";
        }
    }
}
