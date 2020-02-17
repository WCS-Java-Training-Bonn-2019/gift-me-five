package com.gift_me_five;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class GiftMeFive {

	public static void main(String[] args) {
		SpringApplication.run(GiftMeFive.class, args);

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * Outputs a message to console (*** message ***) and waits for timeout seconds
	 * @param message for output
	 * @param timeout in seconds (optional)
	 */
	public static void debugOut(Object message) {
		System.out.println();
		System.out.println();
		System.out.println("*".repeat(120));
		System.out.println(message);
		System.out.println("*".repeat(120));
		System.out.println();
		System.out.println();
	}

	/**
	 * Outputs a message to console (*** message ***) and waits for timeout seconds
	 * @param message for output
	 * @param timeout in seconds (optional)
	 */
	public static void debugOut(Object message, int timeout) {
		System.out.println();
		System.out.println();
		System.out.println("*".repeat(120));
		System.out.println(message);
		System.out.println("*".repeat(120));
		System.out.println();
		System.out.println();

		try {
			Thread.sleep(timeout * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
