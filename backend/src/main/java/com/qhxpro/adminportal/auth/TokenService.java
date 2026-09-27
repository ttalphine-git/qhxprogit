package com.qhxpro.adminportal.auth;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private static final Duration SESSION_TTL = Duration.ofHours(8);

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public Session createSession(String username, String role) {
        var tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        var token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        var session = new Session(token, username, role, Instant.now().plus(SESSION_TTL));
        sessions.put(token, session);
        return session;
    }

    public Optional<Session> findValidSession(String token) {
        var session = sessions.get(token);
        if (session == null) {
            return Optional.empty();
        }
        if (session.expiresAt().isBefore(Instant.now())) {
            sessions.remove(token);
            return Optional.empty();
        }
        return Optional.of(session);
    }

    public record Session(String token, String username, String role, Instant expiresAt) {
    }
}
