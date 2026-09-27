package com.qhxpro.adminportal.user;

import com.qhxpro.adminportal.auth.SuperAdminProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SuperAdminSeeder implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SuperAdminProperties superAdminProperties;

    public SuperAdminSeeder(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            SuperAdminProperties superAdminProperties
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.superAdminProperties = superAdminProperties;
    }

    @Override
    public void run(String... args) {
        var account = superAdminProperties.account();
        if (!StringUtils.hasText(account.password())) {
            throw new IllegalStateException("SUPERADMIN_PASSWORD must be set before starting the application.");
        }

        var encodedPassword = passwordEncoder.encode(account.password());
        var user = userRepository.findByUsernameIgnoreCase(account.username())
                .orElseGet(() -> new AppUser(account.username(), encodedPassword, UserRole.SUPER_ADMIN));

        user.setUsername(account.username());
        user.setPasswordHash(encodedPassword);
        user.setRole(UserRole.SUPER_ADMIN);
        user.setEnabled(true);
        userRepository.save(user);
    }
}
