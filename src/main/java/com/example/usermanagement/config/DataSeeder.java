package com.example.usermanagement.config;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .email("admin@example.com")
                    .phoneNumber("1234567890")
                    .role(Role.ADMIN)
                    .build();

            User user1 = User.builder()
                    .email("user1@example.com")
                    .phoneNumber("0987654321")
                    .role(Role.USER)
                    .build();

            userRepository.save(admin);
            userRepository.save(user1);

            System.out.println("Dummy users seeded.");
        }
    }
}
