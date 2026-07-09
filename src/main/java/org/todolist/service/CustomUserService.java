//package org.todolist.service;
//
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.todolist.dto.AuthRequest;
//import org.todolist.dto.AuthResponse;
//import org.todolist.dto.RegisterRequest;
//import org.todolist.repository.dao.UserDao;
//import org.todolist.repository.entity.DbUser;
//import org.todolist.utill.JwtUtil;
//
//import java.util.Collections;
//
//@Service
//public class CustomUserService {
//
//    private final UserDao userDao;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//
//    public CustomUserService(UserDao userDao,
//                             PasswordEncoder passwordEncoder,
//                             JwtUtil jwtUtil) {
//        this.userDao = userDao;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtUtil = jwtUtil;
//    }
//
//    public AuthResponse register(RegisterRequest req) {
//        if (req == null || req.getUsername() == null || req.getEmail() == null || req.getPassword() == null) {
//            throw new IllegalArgumentException("username, email and password are required");
//        }
//
//        if (userDao.existsByUsername(req.getUsername())) {
//            throw new IllegalArgumentException("username_taken");
//        }
//        if (userDao.existsByEmail(req.getEmail())) {
//            throw new IllegalArgumentException("email_taken");
//        }
//
//        String role = (req.getRole() == null || req.getRole().isBlank()) ? "ROLE_USER" : req.getRole();
//        String encoded = passwordEncoder.encode(req.getPassword());
//        DbUser user = new DbUser(req.getUsername(), req.getEmail(), encoded, role);
//        userDao.save(user);
//
//        UserDetails ud = new User(user.getUsername(), user.getPassword(),
//                Collections.singleton(new SimpleGrantedAuthority(user.getRole())));
//
//        String token = jwtUtil.generateToken(ud);
//        return new AuthResponse(token);
//    }
//
//    public AuthResponse login(AuthRequest req) {
//        if (req == null || req.getUsername() == null || req.getPassword() == null) {
//            throw new IllegalArgumentException("username and password required");
//        }
//
//        DbUser user = userDao.findByUsername(req.getUsername())
//                .orElseThrow(() -> new IllegalArgumentException("invalid_credentials"));
//
//        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
//            throw new IllegalArgumentException("invalid_credentials");
//        }
//
//        UserDetails ud = new User(user.getUsername(), user.getPassword(),
//                Collections.singleton(new SimpleGrantedAuthority(user.getRole())));
//
//        String token = jwtUtil.generateToken(ud);
//        return new AuthResponse(token);
//    }
//}