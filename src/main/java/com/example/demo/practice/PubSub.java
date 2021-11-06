package com.example.demo.practice;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubSub {

    private static final Logger logger = LoggerFactory.getLogger(String.class);
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
        Publisher<Integer> mapPub = mapPub(pub, a -> a*10);
        Subscriber<Integer> logSub = logSub();
        mapPub.subscribe(logSub);
    }
    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f){
        return new Publisher<Integer>(){
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        sub.onNext(f.apply(integer));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        sub.onError(throwable);
                    }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                    }
                });

            }
        };
    }

    private static Subscriber<Integer> logSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                logger.debug("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                logger.debug("on Next :{}", integer);

            }

            @Override
            public void onError(Throwable throwable) {
                logger.debug("on Error : {}", throwable);
            }

            @Override
            public void onComplete() {
                logger.debug("on Complete");
            }
        };
    }

    private static Publisher<Integer> iterPub(List<Integer> iter) {
        return new Publisher<Integer>() {

            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        try{
                            iter.forEach(s->subscriber.onNext(s));
                            subscriber.onComplete();
                        }catch(Throwable t){
                            subscriber.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }
}
