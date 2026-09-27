package com.qhxpro.adminportal.auth;

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
    private final SuperAdminProperties superAdminProperties;

    public AuthController(
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            SuperAdminProperties superAdminProperties
    ) {
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.superAdminProperties = superAdminProperties;
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var admin = superAdminProperties.account();
        if (!admin.username().equalsIgnoreCase(request.username())
                || !passwordEncoder.matches(request.password(), admin.password())) {
            throw new InvalidCredentialsException();
        }

        var session = tokenService.createSession(admin.username(), "SUPER_ADMIN");
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
