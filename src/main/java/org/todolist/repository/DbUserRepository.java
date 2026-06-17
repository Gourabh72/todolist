package org.todolist.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.todolist.repository.entity.DbUser;

import java.util.Optional;

@Repository
public interface DbUserRepository extends MongoRepository<DbUser, String> {
    Optional<DbUser> findByUsername(String username);
    Optional<DbUser> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
