package com.pismo.account.config;

import com.pismo.account.domain.entity.OperationType;
import com.pismo.account.domain.entity.Role;
import com.pismo.account.domain.entity.User;
import com.pismo.account.repository.OperationTypeRepository;
import com.pismo.account.repository.RoleRepository;
import com.pismo.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final OperationTypeRepository operationTypeRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("dev")
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("Initializing database with seed data...");

            // Initialize Operation Types
            if (operationTypeRepository.count() == 0) {
                operationTypeRepository.save(new OperationType(1L, "PURCHASE"));
                operationTypeRepository.save(new OperationType(2L, "INSTALLMENT PURCHASE"));
                operationTypeRepository.save(new OperationType(3L, "WITHDRAWAL"));
                operationTypeRepository.save(new OperationType(4L, "PAYMENT"));
                log.info("Operation types initialized");
            }

            // Initialize Roles
            Role roleUser = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("ROLE_USER");
                        return roleRepository.save(role);
                    });

            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("ROLE_ADMIN");
                        return roleRepository.save(role);
                    });
            log.info("Roles initialized");

            // Initialize Default Users
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@pismo.com");
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setEnabled(true);
                admin.setRoles(Set.of(roleUser, roleAdmin));
                userRepository.save(admin);
                log.info("Admin user created");
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setEmail("user@pismo.com");
                user.setPassword(passwordEncoder.encode("password123"));
                user.setEnabled(true);
                user.setRoles(Set.of(roleUser));
                userRepository.save(user);
                log.info("Regular user created");
            }

            log.info("Database initialization completed!");
        };
    }
}
