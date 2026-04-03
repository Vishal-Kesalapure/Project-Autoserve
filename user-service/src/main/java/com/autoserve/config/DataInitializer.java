package com.autoserve.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.autoserve.entity.Admin;
import com.autoserve.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "Admin@123";

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) {
		if (userService.existsByUsername(ADMIN_USERNAME)) {
			log.info("Default admin already exists — skipping seed.");
			return;
		}

		Admin admin = new Admin();
		admin.setUsername(ADMIN_USERNAME);
		admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
		admin.setFirstName("Super");
		admin.setLastName("Admin");
		admin.setMobileNumber("9000000000");
		admin.setAccountEnabled(true);

		userService.saveAdmin(admin);
		log.info("Default admin seeded — username: '{}'", ADMIN_USERNAME);
	}
}
