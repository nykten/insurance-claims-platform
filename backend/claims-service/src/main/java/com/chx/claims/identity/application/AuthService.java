package com.chx.claims.identity.application;

import com.chx.claims.identity.domain.User;
import com.chx.claims.identity.infra.UserRepository;
import com.chx.claims.identity.security.JwtService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public Map<String, Object> login(String email, String password) {
        User user = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return Map.of("token", jwt.issue(user), "user", Map.of(
                "id", user.getId(), "email", user.getEmail(),
                "fullName", user.getFullName(), "role", user.getRole()));
    }
}
