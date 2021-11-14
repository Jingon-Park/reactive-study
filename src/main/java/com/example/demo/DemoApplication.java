package com.example.demo;




import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@EnableAsync
@Slf4j
public class DemoApplication {

    @RestController
    public static class MyController{

        @GetMapping("/callable")
        public Callable<String> callable() throws InterruptedException{
            log.info("callable");

            return () -> {
                log.info("async");
                Thread.sleep(2000);

                return "hello";
            };
        }
    }
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

    public static void main(String[] args) {
        // try (ConfigurableApplicationContext c = SpringApplication.run(DemoApplication.class, args)) {
        // }
        SpringApplication.run(DemoApplication.class, args);
    }

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

}
