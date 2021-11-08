package com.example.demo.reactive;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import ch.qos.logback.core.util.ExecutorServiceUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class SchedulerEx {

    /*
    이런 코드는 main 스레드에서 모든 코드들이 직렬적으로 실행된다.
    위와 같은 코든느 핵심 스레드가 블로킹하는 방식으로 동작한다.
    이런 방식은 효율이 떨어진다. 실제는 퍼블리셔와 서브스크라이버를 같은 스레드에서 작성하지 않는다.
    scheduler를 통해서 위 문제를 해결할 수 있음.
    */

    public static void main(String[] args) {
        ArrayList<String> data = new ArrayList<>();
        data.add("test");
        Mono<String> test = Mono.just("test")
                .single();
        test.subscribe(System.out::println);
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {

                @Override
                public void request(long n) {
                    // TODO Auto-generated method stub
                    log.debug("request");
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onComplete();


                    
                }

                @Override
                public void cancel() {
                    // TODO Auto-generated method stub
                    
                }
                
            });
        };

        Publisher<Integer> subOnPub = sub ->{
            ExecutorService es = Executors.newSingleThreadExecutor();
            es.execute(() -> pub.subscribe(sub));
        };

        Publisher<Integer> pubOnSeb = sub ->{
            pub.subscribe(new Subscriber<Integer>() {

                ExecutorService ex = Executors.newSingleThreadExecutor();
                @Override
                public void onSubscribe(Subscription s) {
                    // TODO Auto-generated method stub
                    sub.onSubscribe(s);
                    
                }

                @Override
                public void onNext(Integer t) {
                    // TODO Auto-generated method stub
                    ex.execute(()-> sub.onNext(t));
                }

                @Override
                public void onError(Throwable t) {
                    // TODO Auto-generated method stub
                    ex.execute(()-> sub.onError(t));
                    
                }

                @Override
                public void onComplete() {
                    // TODO Auto-generated method stub
                    ex.execute(()-> sub.onComplete());
                }
                
            });
        };

        Subscriber<Integer> sub = new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription s) {
                // TODO Auto-generated method stub
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer t) {
                // TODO Auto-generated method stub
                log.debug("onNext : {}", t);
            }

            @Override
            public void onError(Throwable t) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onComplete() {
                // TODO Auto-generated method stub
                log.debug("onComplete");
            }
            
        };
        //pubOnSeb.subscribe(sub);
        log.debug("exit");
    }
}
