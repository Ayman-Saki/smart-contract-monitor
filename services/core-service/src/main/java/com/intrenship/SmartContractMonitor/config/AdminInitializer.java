package com.intrenship.SmartContractMonitor.config;

import com.intrenship.SmartContractMonitor.entities.User;
import com.intrenship.SmartContractMonitor.enums.Role;
import com.intrenship.SmartContractMonitor.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        String email = "admin@smartcontract.com";

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
        "ADMIN_PASSWORD must be configured before starting the application."
    );
}

        if (!userRepository.existsByEmail(email)) {

            User user = new User();

            user.setUsername("admin");
            user.setEmail(email);

            user.setPassword(
                    passwordEncoder.encode(
                            adminPassword
                    )
            );

            user.setRole(Role.ADMIN);

            userRepository.save(user);

            System.out.println(
                    "Default admin account created"
            );
        }
    }
}