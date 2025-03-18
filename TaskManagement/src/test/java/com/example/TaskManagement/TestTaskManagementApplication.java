package com.example.TaskManagement;

import org.springframework.boot.SpringApplication;

public class TestTaskManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(TaskManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
