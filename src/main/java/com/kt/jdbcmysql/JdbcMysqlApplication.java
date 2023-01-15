package com.kt.jdbcmysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.kt.jdbcmysql.demo.Demo;

@SpringBootApplication
public class JdbcMysqlApplication {

	@Autowired
	private Demo demo;

	public static void main(String[] args) {
		SpringApplication.run(JdbcMysqlApplication.class, args);
	}

	// run demo
	@Bean
	CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println("\n\nDEMO\n");
			demo.runDemo();
		};
	}
}
