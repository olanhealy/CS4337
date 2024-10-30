package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class DemoApplication {
	private final JdbcTemplate jdbcTemplate;
	private static final String[] names = {"Mehdi", "Linus", "Thor", "Rossman", "Alec"};

	@GetMapping("/export-data")
	public Map<String, Object> exportData() {
		// Request start time
		long startTime = System.currentTimeMillis();
		int name = (int) (Math.random() * names.length);
		int success;
		// "Exports" the data by performing an insert and get query
		try {
			success = jdbcTemplate.update("INSERT INTO users (email) VALUES (?)", names[name]);
			jdbcTemplate.execute("SELECT * FROM users");
		} catch (DataAccessException e) {
			jdbcTemplate.execute("USE testing;");
			success = jdbcTemplate.update("INSERT INTO users (email) VALUES (?)", names[name]);
			jdbcTemplate.execute("SELECT * FROM users");
		}
		// Request end time
		long endTime = (System.currentTimeMillis() - startTime) % 1000;

		return Map.of("success",success,"message", "Exported data successfully", "time (ms)", endTime);
	}

	@Autowired
	public DemoApplication(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@GetMapping("/hello")
	public Map<String, Object> sayHello(){
		return Map.of("message","Hello World!");
	}

	@GetMapping ( "/users" )
	public List<Map<String, Object>> users() {
		List<Map<String, Object>> users;
		try {
			users = jdbcTemplate.queryForList("SELECT * FROM users");
		}catch (DataAccessException e){
			jdbcTemplate.execute("USE testing;");
			jdbcTemplate.execute("CREATE table users(id INT AUTO_INCREMENT,emailVARCHAR(255),PRIMARYKEY(id))");
			users=jdbcTemplate.queryForList("SELECT * FROM users");
		}
		return users;
	}
	@PostMapping("/user")
	public Map<String, Object> createUser(@RequestBody Map<String, String> fields) {
		int success = jdbcTemplate.update("INSERT INTO users (email) VALUES (?)", fields.get("email"));
		return Map.of("success",success, "email",fields.get("email"));
	}
	@DeleteMapping("/user")
	public Map<String, Object> deleteUser(@RequestBody Map<String, String> fields) {
		int success = jdbcTemplate.update("DELETE FROM users  WHERE (id) = (?)", fields.get("id"));
		return Map.of("success",success, "id",fields.get("id"));
	}
	@PutMapping("/user")
	public Map<String, Object> updateUser(@RequestBody Map<String, String> fields) {
		int success = jdbcTemplate.update("UPDATE users SET email = (?) WHERE id = (?)", fields.get("email"), fields.get("id"));
		return Map.of("success",success, "email",fields.get("email"));
	}
}

