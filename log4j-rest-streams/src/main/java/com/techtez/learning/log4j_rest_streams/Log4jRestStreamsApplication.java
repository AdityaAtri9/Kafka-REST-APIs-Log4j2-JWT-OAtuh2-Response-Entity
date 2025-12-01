package com.techtez.learning.log4j_rest_streams;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.techtez.learning")
public class Log4jRestStreamsApplication {

	private static final Logger logger = LogManager.getLogger(Log4jRestStreamsApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(Log4jRestStreamsApplication.class, args);
		logger.info("Application started — log file should be created now!");
	}

}
