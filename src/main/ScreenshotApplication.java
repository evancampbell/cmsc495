package main;

import main.db.Role;
import main.db.RoleRepository;
import main.db.User;
import main.db.UserRepository;
import main.properties.GlobalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ScreenshotApplication extends SpringBootServletInitializer {

	public final Logger log = LoggerFactory.getLogger(ScreenshotApplication.class);

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private GlobalProperties properties;

	public static void main(String[] args)

	{
		SpringApplication.run(ScreenshotApplication.class, args);
	}

	@Bean
	public Logger getLogger() { return log; }

	@Bean
	CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository) {
		return args -> {
			Role adminRole = roleRepository.findByRole("admin");
			if (adminRole == null) {
				Role newAdminRole = new Role();
				newAdminRole.setRole("admin");
				roleRepository.save(newAdminRole);
			}

			Role userRole = roleRepository.findByRole("user");
			if (userRole == null) {
				Role newUserRole = new Role();
				newUserRole.setRole("user");
				roleRepository.save(newUserRole);
			}

			User adminUser = userRepository.findByEmail(properties.getAdmin());
			if (adminUser == null) {
				User newAdmin = new User();
				newAdmin.setEmail(properties.getAdmin());
				newAdmin.setPassword(passwordEncoder.encode(properties.getPassword()));
				newAdmin.setRole("admin");
				userRepository.save(newAdmin);
			}
		};

	}
		
}


	
	



