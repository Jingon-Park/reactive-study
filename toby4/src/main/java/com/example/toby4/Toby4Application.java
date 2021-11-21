package com.example.toby4;

import java.util.concurrent.Callable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;


/**
 * 비동기로 작업을 실행해도 작업 스레드는 작업의 갯수만큼 생성될텐데 서블릿 스레드를 생성하는거랑 뭐가 다른가?
 * : 작업 스레드가 생성되어 오래 걸리는 작업을 수행한다면 크게 의미가 없음. 지금까지는 하나의 서블릿 스레드로 비동기적으로 처리하니까
 * 한번에 많은 요청을 처리할 수 있는 준비가 되어 있다라고 생각하자.
 */
@SpringBootApplication
@Slf4j
public class Toby4Application {


	// @Component
    // public static class MyService {
    //     @Async
    //     public ListenableFuture<String> hello() throws InterruptedException {
    //         log.info("hello()");
    //         try{
    //             Thread.sleep(2000);
    //         }catch(InterruptedException e){
    //             e.printStackTrace();
    //         }
         
    //         log.info("end");
    //         return new AsyncResult<>( "Hello");
    //     }
    // }
    // @Bean // @Async 대신 사용할 수 있는 방법
    // ThreadPoolTaskExecutor tp() {
    //     ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
    //     te.setCorePoolSize(10);
    //     te.setMaxPoolSize(100);
    //     te.setQueueCapacity(200);
    //     te.setThreadNamePrefix("mythread");
    //     te.initialize();
    //     return te;
    // }
	// @Autowired
    // MyService myService;
 
    // @Bean
    // ApplicationRunner run() {
    //     return args -> {
    //         log.info("run()");
    //         ListenableFuture<String> f = myService.hello();
    //         f.addCallback(s-> System.out.println(s), e-> System.out.println("test: " + e.getMessage()));
    //         log.info("exit");
    //     };
    // }

	@RestController
	public static class MyController{

		@GetMapping("callable")
		public Callable<String> callable() {
			log.info("callable");

			return ()->{
				log.info("async");
				Thread.sleep(2000);

				return "Hello";
			};
		}

        // @GetMapping("callable")
		// public String callable() throws InterruptedException {
		// 	log.info("callable");

		// 	Thread.sleep(2000);

        //     return "hello";
		// }
	}

	public static void main(String[] args) {
		// try (ConfigurableApplicationContext c = SpringApplication.run(DemoApplication.class, args)) {
        // }
		SpringApplication.run(Toby4Application.class, args);
	}

}
