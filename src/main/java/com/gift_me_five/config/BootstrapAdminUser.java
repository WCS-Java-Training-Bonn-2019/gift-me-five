package com.gift_me_five.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gift_me_five.entity.User;
import com.gift_me_five.repository.UserRepository;

@Component
public class BootstrapAdminUser implements CommandLineRunner {

	@Value("${admin.username}")
	private String adminUsername;
	@Value("${admin.password}")
	private String adminPassword;

	private final UserRepository userRepository;

	public BootstrapAdminUser(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		Optional<User> optionalAdmin = userRepository.findByEmail("admin");
		if (optionalAdmin.isEmpty()) {
			System.out.println();
			System.out.println("*".repeat(80));
			System.out.println("No admin found, creating default admin");
			System.out.println();
			User admin = new User();
			admin.setEmail(adminUsername);
			admin.setRole("admin");
			admin.setPassword(adminPassword);
			System.out.println(admin.toString());
			System.out.println("*".repeat(80));
			System.out.println();
			userRepository.save(admin);
		}
	}
}
