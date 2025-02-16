package com.harshapps.codetogether;

import com.harshapps.codetogether.logging.LogStreamAppender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * CodeTogether Application Class
 */
@SpringBootApplication
public class CodetogetherApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CodetogetherApplication.class, args);
	}

	/**
	 * Run Method for any manual runs
	 * @param args incoming main method arguments
	 * @throws Exception Any Exceptions in Runner
	 */
	@Override
	public void run(String... args) throws Exception {
		// Register the custom log appender when the application starts
//		LogStreamAppender.createAppender();
	}
}
