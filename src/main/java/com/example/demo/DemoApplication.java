package com.example.demo;




import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableAsync
@Slf4j
public class DemoApplication {
    @Component
    public static class MyService {
        @Async
        public ListenableFuture<String> hello() throws InterruptedException {
            log.debug("hello()");
            Thread.sleep(2000);
            log.debug("end");
            return new AsyncResult<>( "Hello");
        }
    }
    @Bean // @Async 대신 사용할 수 있는 방법
    ThreadPoolTaskExecutor tp() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(10);
        te.setMaxPoolSize(100);
        te.setQueueCapacity(200);
        te.setThreadNamePrefix("mythread");
        te.initialize();
        return te;
    }
    public static void main(String[] args) {
        try (ConfigurableApplicationContext c = SpringApplication.run(DemoApplication.class, args)) {
        }
    }
    @Autowired
    MyService myService;
    @Bean
    ApplicationRunner run() {
        return args -> {
            log.info("run()");
            ListenableFuture<String> f = myService.hello();
            f.addCallback(s-> System.out.println(s), e-> System.out.println("test: " + e.getMessage()));
            log.info("exit");
        };
    }


//    private static final Logger logger = LoggerFactory.getLogger(String.class);
//
//    @RestController
//    public static class Controller {
//
//        @RequestMapping("/hello")
//        public Publisher<String> hello(String name) {
//            return new Publisher<String>() {
//                public void subscribe(Subscriber<? super String> sub) {
//                    System.out.println("subscribe");
//                    sub.onSubscribe(new Subscription() {
//
//                        public void request(long n) {
//                            sub.onNext(name);
//                            sub.onComplete();
//                        }
//
//                        public void cancel() {
//
//                        }
//                    });
//                }
//            };
//
//        }
//
//    }
//    @Component
//    public static class MyService {
//
//        @Async("tp")//실전에서 Thread 풀을 컨트롤하는 @Bean등록없이 @Async를 사용하면 안된다. 계속 스레드 생성함
//        public ListenableFuture<String> hello() throws InterruptedException {
//            logger.debug("hello()");
//            Thread.sleep(2000);
//            return new AsyncResult<>("Hello");
//        }
//    }
//
//    @Bean
//    ThreadPoolTaskExecutor tp() {
//        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
//        te.setCorePoolSize(10);
//        te.setMaxPoolSize(200);
//        te.setQueueCapacity(100);
//        te.setThreadNamePrefix("My-Thread-");
//        te.initialize();
//        return te;
//    }
//
//    public static void main(String[] args) {
//        try (ConfigurableApplicationContext c = SpringApplication.run(DemoApplication.class,
//            args)) {
//
//        }
//
//    }
//
//    @Autowired MyService myService;
//
//    @Bean
//    ApplicationRunner run() {
//        return args -> {
//            logger.info("run()");
//            ListenableFuture<String> f = myService.hello();
//            f.addCallback(s -> System.out.println(s), e -> System.out.println(e.getMessage()));
//
//            logger.info("exit");
//        };
//    }

}
