package com.harshapps.codetogether;

import com.harshapps.codetogether.logging.LogStreamAppender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CodetogetherApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CodetogetherApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Register the custom log appender when the application starts
//		LogStreamAppender.createAppender();
	}
}
