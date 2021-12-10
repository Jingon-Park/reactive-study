package com.example.toby7;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@Slf4j
public class Toby7Application {

	@RestController
	public class MyController {
		//RestTemplate rt = new RestTemplate(); // 블로킹 방식  => 각각 2초 작업시간이 걸리는 100개의 API 호출 시에 하나의 쓰레드로 처리하기 때문에 약 200초 걸림
		//AsyncRestTemplate rt = new AsyncRestTemplate(); // 논블로킹 방식 => 각각 2초 작업시간이 걸리는 100개의 API 호출 시에 100개의 쓰레드로 처리하기 때문에 약 2초 걸림
		AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1))); // 논블로킹 방식 => 각각 2초 작업시간이 걸리는 1
		static final String URL1 = "http://localhost:8081/service?req={req}";
		static final String URL2 = "http://localhost:8081/service2?req={req}";

		@Autowired
		MyService myService;

		@GetMapping("/rest")
		public DeferredResult<String> rest(int idx) {   // 결과를 가공하여 리턴 또는 다른 API와 의존적인 관계로 만드는 법
			DeferredResult<String> dr = new DeferredResult<>();
//			Completion
//					.from(rt.getForEntity(URL1, String.class, "h" + idx))
//					.andApply(s->rt.getForEntity(URL2, String.class, s.getBody()))
//					.andApply(s->myService.work(s.getBody()))
//					.andError(e->dr.setErrorResult("Error [" + e.toString() + "]"))
//					.andAccept(s->dr.setResult(s));

			toCF(rt.getForEntity(URL1, String.class, "hello" + idx))
					.thenCompose(s -> toCF(rt.getForEntity(URL2, String.class, s.getBody())))
					.thenApplyAsync(s2 -> myService.work(s2.getBody()))
					.thenAccept(s3 -> dr.setResult(s3))
					.exceptionally(e ->{dr.setErrorResult(e.getMessage());
						return (Void) null;});

			return dr;


		}
	}

	<T> CompletableFuture<T> toCF(ListenableFuture<T> lf){
		CompletableFuture<T> cf = new CompletableFuture<>();
		lf.addCallback(s ->{
			cf.complete(s);
		}, ex -> {
			cf.completeExceptionally(ex);
		});

		return cf;
	}




	@Service
	public static class MyService {
		public String work(String req) {
			return req + "/asyncwork";
		}
	}

	@Bean
	ThreadPoolTaskExecutor myThreadPool() {  //
		ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
		te.setCorePoolSize(1);
		te.setMaxPoolSize(1);
		te.initialize();
		return te;
	}





	public static void main(String[] args) {
		System.setProperty("server.port", "8080");
		System.setProperty("server.tomcat.max-threads", "1");
		SpringApplication.run(Toby7Application.class, args);
	}

}
