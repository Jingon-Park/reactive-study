package com.example.demo.tody5;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j

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
	public static class MainController {
		// asynchronous
		AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

		@GetMapping("/rest")
		public ListenableFuture<ResponseEntity<String>> rest(int idx) {
			return rt.getForEntity("http://localhost:8081/service?req={req}",
					String.class, "hello" + idx);
		}
	}


	

	public static void main(String[] args) {
		System.setProperty("server.port", "8080");
		System.setProperty("server.tomcat.max-threads", "1");
		SpringApplication.run(Application.class, args);
	}

}
