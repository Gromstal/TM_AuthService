package org.example.taskmanager_authservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TaskManagerAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagerAuthServiceApplication.class, args);
	}

}
