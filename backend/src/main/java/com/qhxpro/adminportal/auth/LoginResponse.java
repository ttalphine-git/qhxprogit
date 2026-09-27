package com.qhxpro.adminportal.auth;

public record LoginResponse(
        String token,
        String username,
        String role
) {
}
