//package org.todolist.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.todolist.exception.SessionExpiredException;
//import org.todolist.utill.JwtUtil;
//import tools.jackson.databind.ObjectMapper;
//
//import java.io.IOException;
//import java.util.Map;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = authHeader.substring(7);
//        String username;
//        try {
//            username = jwtUtil.extractUsername(token);
//        } catch (SessionExpiredException ex) {
//            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "SESSION_EXPIRED", ex.getMessage());
//            return;
//        } catch (Exception ex) {
//            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN", "Token is invalid");
//            return;
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            try {
//                boolean valid = jwtUtil.validateToken(token, userDetails);
//                if (valid) {
//                    UsernamePasswordAuthenticationToken auth =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                } else {
//                    sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN", "Token validation failed");
//                    return;
//                }
//            } catch (SessionExpiredException ex) {
//                sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "SESSION_EXPIRED", ex.getMessage());
//                return;
//            } catch (Exception ex) {
//                sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN", "Token validation error");
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private void sendJsonError(HttpServletResponse response, int status, String error, String message) throws IOException {
//        response.setStatus(status);
//        response.setContentType("application/json");
//        Map<String, String> body = Map.of(
//                "error", error,
//                "message", message == null ? "" : message
//        );
//        response.getWriter().write(mapper.writeValueAsString(body));
//    }
//}
