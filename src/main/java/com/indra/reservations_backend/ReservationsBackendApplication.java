package com.indra.reservations_backend;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class ReservationsBackendApplication implements CommandLineRunner {

	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ReservationsBackendApplication.class, args);
	}

	@Override
	public void run(String... args) {
		String rawPassword = "admin123";
		String encodedPassword = passwordEncoder.encode(rawPassword);
		System.out.println("Generated BCrypt hash for '" + rawPassword + "': " + encodedPassword);
	}
}


