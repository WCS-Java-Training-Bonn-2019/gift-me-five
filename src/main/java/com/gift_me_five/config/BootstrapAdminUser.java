package com.gift_me_five.config;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.gift_me_five.entity.User;
import com.gift_me_five.repository.UserRepository;

@Component
public class BootstrapAdminUser implements CommandLineRunner{

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public BootstrapAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
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
			admin.setEmail("admin");
			admin.setRole("admin");
			admin.setPassword(passwordEncoder.encode("admin"));
			System.out.println(admin.toString());
			System.out.println("*".repeat(80));
			System.out.println();
			userRepository.save(admin);
		}
	}
}
