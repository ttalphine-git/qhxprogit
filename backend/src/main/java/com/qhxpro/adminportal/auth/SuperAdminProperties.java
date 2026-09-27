package com.qhxpro.adminportal.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SuperAdminProperties(Account account) {

    public record Account(String username, String password) {
    }
}
