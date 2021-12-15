package com.toby.toby9;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@SpringBootApplication
public class Toby9Application {


	@Bean
	public NioEventLoopGroup nioEventLoopGroup() {
		return new NioEventLoopGroup(1);
	}

	@RestController
	public static class MyController{
		@GetMapping("/rest")
		public Mono<String> hello(String idx) {
			Mono<String> m = Mono.just("hellod").log();
			return m;
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(Toby9Application.class, args);
	}

}
