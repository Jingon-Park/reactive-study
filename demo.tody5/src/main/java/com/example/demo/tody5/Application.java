package com.example.demo.tody5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class Application {

	@RestController
	public class MyController{

		RestTemplate rt = new RestTemplate();

		@GetMapping("/rest")
		public String rest(int idx) {
			log.info("Application Start");
			return rt.getForObject("http://localhost:8081/service?req={req}", String.class, "hello" + idx);
		}
	}


	

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
