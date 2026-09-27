package com.qhxpro.adminportal.auth;

import com.qhxpro.adminportal.user.AppUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AppUserRepository userRepository;

    public AuthController(
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            AppUserRepository userRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var user = userRepository.findByUsernameIgnoreCase(request.username())
                .filter(found -> found.isEnabled()
                        && passwordEncoder.matches(request.password(), found.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        if (!"SUPER_ADMIN".equals(user.getRole().name())) {
            throw new InvalidCredentialsException();
        }

        var session = tokenService.createSession(user.getUsername(), user.getRole().name());
        return new LoginResponse(session.token(), session.username(), session.role());
    }

    @GetMapping("/me")
    public ProfileResponse me(@AuthenticationPrincipal SuperAdminPrincipal principal) {
        return new ProfileResponse(principal.username(), principal.role());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private static class InvalidCredentialsException extends RuntimeException {
    }
}
