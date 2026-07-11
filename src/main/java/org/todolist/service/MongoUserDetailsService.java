package org.todolist.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.todolist.repository.DbUserRepository;
import org.todolist.repository.dao.UserDao;
import org.todolist.repository.entity.DbUser;

import java.util.Collections;

@Service
public class MongoUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    public MongoUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DbUser dbUser = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(
                dbUser.getUsername(),
                dbUser.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(dbUser.getRole()))
        );
    }
}
