package com.bobocode.tudaleasing.config;

import com.bobocode.tudaleasing.entity.User;
import com.bobocode.tudaleasing.entity.enums.Role;
import com.bobocode.tudaleasing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Runs on startup:
 * 1. Re-encodes any plain-text passwords that are not yet BCrypt-hashed.
 * 2. Creates a default ADMINISTRATOR account if no admins exist.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final String BCRYPT_PREFIX = "$2";
    private static final String DEFAULT_ADMIN_EMAIL    = "admin@tudaleasing.pl";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin@1234";

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        migratePasswordsIfNeeded();
        createDefaultAdminIfNeeded();
    }

    // ─── Migrate plain-text → BCrypt ─────────────────────────────────────────

    private void migratePasswordsIfNeeded() {
        List<User> users = userRepository.findAll();
        int count = 0;

        for (User user : users) {
            if (!user.getPassword().startsWith(BCRYPT_PREFIX)) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                count++;
            }
        }

        if (count > 0) {
            log.info("[DataInitializer] Migrated {} plain-text password(s) to BCrypt.", count);
        }
    }

    // ─── Ensure at least one admin exists ────────────────────────────────────

    private void createDefaultAdminIfNeeded() {
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == Role.ADMINISTRATOR);

        if (!adminExists) {
            User admin = new User();
            admin.setEmail(DEFAULT_ADMIN_EMAIL);
            admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            admin.setFirstName("Admin");
            admin.setLastName("System");
            admin.setRole(Role.ADMINISTRATOR);
            userRepository.save(admin);
            log.warn("[DataInitializer] No admin found — created default admin: {} / {}",
                    DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD);
        }
    }
}

