package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
@SpringBootApplication
@RestController
public class DemoApplication {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping("/users")
	public List<Map<String, Object>> users() {
		return jdbcTemplate.queryForList("SELECT * FROM users");
	}

	@PostMapping("/user")
	public Map<String, Object> createUser(@RequestBody Map<String, String> fields)
	{
		String email = fields.get("email");
		System.out.println("Received email: " + email);  // Log the received email

		int success = jdbcTemplate.update("INSERT INTO users (email) VALUES (?)", email);
		System.out.println("Insert success: " + success);  // Log if the insert was successful

		return Map.of("success", success, "email", email);
	}
}