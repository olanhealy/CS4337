package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@RestController
public class DemoApplication {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	// Names to enter into the database
	private static final String[] names = {"Mehdi", "Linus", "Thor", "Rossman", "Alec"};

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping("/export-data")
	public ResponseEntity<Map<String, Object>> exportData() {
		String exportId =  UUID.randomUUID().toString();
		String exportRequest = String.format("{\"exportId\":\"%s\",\"timestamp\":\"%s\"}",
				exportId, LocalDateTime.now().toString());

		Map<String, Object> response = new HashMap<>();
		response.put("status", "queued");
		response.put("message", "Export request queued for processing");
		response.put("exportId", exportId);
		response.put("timestamp", LocalDateTime.now().toString());

		return ResponseEntity.accepted().body(response);
	}





	@GetMapping("/users")
	public List<Map<String, Object>> users() {
		List<Map<String, Object>> users;
		try {
			users = jdbcTemplate.queryForList("SELECT * FROM users");
		} catch (DataAccessException e) {
			jdbcTemplate.execute("USE testing;");
			jdbcTemplate.execute("CREATE TABLE users (id INT AUTO_INCREMENT, email VARCHAR(255), PRIMARY KEY(id))");
			users = jdbcTemplate.queryForList("SELECT * FROM users");
		}
		return users;
	}

	@PostMapping("/user")
	public Map<String, Object> createUser(@RequestBody Map<String, String> fields) {
		int success = jdbcTemplate.update(
				"INSERT INTO users (email) VALUES (?)", fields.get("email"));
		return Map.of("success", success, "email", fields.get("email"));
	}

	@GetMapping("/hello")
	public Map<String, Object> sayHello() {
		return Map.of("message", "Hello World!");
	}

	@DeleteMapping("/user")
	public Map<String, Object> deleteUser(@RequestBody Map<String, String> fields) {
		int success = jdbcTemplate.update(
				"DELETE FROM users WHERE id = ?", fields.get("id"));
		return Map.of("success", success, "id", fields.get("id"));
	}

	@PutMapping("/user")
	public Map<String, Object> updateUser(@RequestBody Map<String, String> fields) {
		int success = jdbcTemplate.update(
				"UPDATE users SET email = ? WHERE id = ?", fields.get("email"), fields.get("id"));
		return Map.of("success", success, "email", fields.get("email"));
	}
}
