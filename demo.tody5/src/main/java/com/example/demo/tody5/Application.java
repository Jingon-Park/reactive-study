package com.example.demo.tody5;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.async.DeferredResult;

@SpringBootApplication
@Slf4j
@EnableAsync
public class Application {


	
//	@RestController
//	public class MyController{
//
//		//RestTemplate rt = new RestTemplate();
//		AsyncRestTemplate rt = new AsyncRestTemplate();
//
//		/**
//		 * 이전 강의 4강에서 배운 내용인 Callable이나 DeferredResult의 경우
//		 * DefferedResult는 외부에서 어떤 이벤트가 발생해야만하고
//		 * Callable은 workerThread가 계속해서 생성되는 문제가 발생한다.
//		 * 이를 Spring 3.0에서는 해결하지 못했다.
//		 * 4.0에서 부터는 AsyncRestTemplate를 제공한다.
//		 * 하지만 AsyncRestTemplate 또한 외부 API를 호출할 때 비동기로 동작하긴 하지만
//		 * 비동기 처리를 위해서 백그라운드에 Thread를 추가적으로 만들기 때문에 서버에는 부하임
//		 */
//		@GetMapping("/rest")
//		public ListenableFuture<ResponseEntity<String>> rest(int idx) {
//			log.info("Application Start");
//			return rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx);
//		}
//	}

	@RestController
	public class MyController {
		//RestTemplate rt = new RestTemplate(); // 블로킹 방식  => 각각 2초 작업시간이 걸리는 100개의 API 호출 시에 하나의 쓰레드로 처리하기 때문에 약 200초 걸림
		//AsyncRestTemplate rt = new AsyncRestTemplate(); // 논블로킹 방식 => 각각 2초 작업시간이 걸리는 100개의 API 호출 시에 100개의 쓰레드로 처리하기 때문에 약 2초 걸림
		AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1))); // 논블로킹 방식 => 각각 2초 작업시간이 걸리는 1


		@Autowired
		MyService myService;

		@GetMapping("/rest")
		public DeferredResult<String> rest(int idx) {   // 결과를 가공하여 리턴 또는 다른 API와 의존적인 관계로 만드는 법
			DeferredResult<String> dr = new DeferredResult<>();
			ListenableFuture<ResponseEntity<String>> f1 = rt.getForEntity("http://localhost:8081/service1?req={req}", String.class, "hello" + idx);
			f1.addCallback(s1->{
				ListenableFuture<ResponseEntity<String>> f2 = rt.getForEntity("http://localhost:8081/service2?req={req}", String.class, s1.getBody());
				f2.addCallback(s2->{
					//dr.setResult(s2.getBody());
					ListenableFuture<String> f3 =  myService.work(s2.getBody());
					f3.addCallback(s3->{
						dr.setErrorResult(s3);
					},e3->{
						dr.setErrorResult(e3.getMessage());
					});
				},e2->{
					dr.setErrorResult(e2.getMessage());
				});
			}, e1->{
				dr.setErrorResult(e1.getMessage());
			});
			return dr;
		}
	}


	@Service
	public static class MyService {
		@Async
		public ListenableFuture<String> work(String req) {
			return new AsyncResult<>(req + "/asyncwork");
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
		SpringApplication.run(Application.class, args);
	}

}
