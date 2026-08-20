package com.codetrix.auth.config;

import com.codetrix.auth.entity.Role;
import com.codetrix.auth.entity.User;
import com.codetrix.auth.repository.RoleRepository;
import com.codetrix.auth.repository.UserRepository;
import com.codetrix.common.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("!test")
    public CommandLineRunner initializeData() {
        return args -> {
            initializeRoles();
            initializeDefaultAdmin();
        };
    }

    private void initializeRoles() {
        if (!roleRepository.existsByName(RoleType.ADMIN)) {
            Role adminRole = Role.builder()
                    .name(RoleType.ADMIN)
                    .description("Administrator role with full access")
                    .build();
            roleRepository.save(adminRole);
            log.info("Created ADMIN role");
        }

        if (!roleRepository.existsByName(RoleType.TEAM)) {
            Role teamRole = Role.builder()
                    .name(RoleType.TEAM)
                    .description("Team role for participants")
                    .build();
            roleRepository.save(teamRole);
            log.info("Created TEAM role");
        }
    }

    private void initializeDefaultAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .displayName("System Administrator")
                    .email("admin@codetrix.com")
                    .role(adminRole)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Created default admin user (username: admin, password: admin123)");
            log.warn("IMPORTANT: Change the default admin password in production!");
        }
    }
}
