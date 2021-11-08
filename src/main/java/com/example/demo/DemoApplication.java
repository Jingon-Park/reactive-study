package com.example.demo;


import com.example.demo.reactive.User;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class DemoApplication {
	
    private static final Logger logger = LoggerFactory.getLogger(String.class);

	@RestController
    public static class Controller{

        @RequestMapping("/hello")
        public Publisher<String> hello(String name){
            return new Publisher<String>(){
                public void subscribe(Subscriber<? super String> sub){
                    System.out.println("subscribe");
                    sub.onSubscribe(new Subscription() {
                        
                        public void request(long n){
                            sub.onNext(name);
                            sub.onComplete();
                        }
                        public void cancel(){

                        }
                    });
                }
            };

        }
    }
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
