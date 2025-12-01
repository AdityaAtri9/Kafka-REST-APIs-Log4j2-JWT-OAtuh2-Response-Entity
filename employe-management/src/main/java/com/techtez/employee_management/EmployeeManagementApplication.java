package com.techtez.employee_management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmployeeManagementApplication {

	private static final Logger logger = LogManager.getLogger(EmployeeManagementApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagementApplication.class, args);
		logger.info("Application started.");
	}

}
