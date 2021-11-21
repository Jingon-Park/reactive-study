package com.example.demo.reactive;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubSub {

    private static final Logger logger = LoggerFactory.getLogger(PubSub.class);
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
        Publisher<String> mapPub = mapPub(pub, a -> "[" + a + "]");
        //Publisher<Integer> sumPub = sumPub(pub);
        Publisher<String> reducePub = reducePub(pub, "", (a, b) -> a + "-" + b);
        Subscriber<Integer> logSub = logSub();
        reducePub.subscribe(logSub());
    }



    public static <T,R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf){

        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub){
                pub.subscribe(new DelegeateSub<T, R>(sub){
                    R result = init;

                    @Override
                    public void onNext(T i){
                        result = bf.apply(result, i);
                    }

                    @Override
                    public void onComplete(){
                        sub.onNext(result);
                        sub.onComplete();
                    }
                });
            }
        };

    }
    
    // private static Publisher<Integer> sumPub(Publisher<Integer> pub){
    //     return new Publisher<Integer>() {

    //         @Override
    //         public void subscribe(Subscriber<? super Integer> s) {
    //             // TODO Auto-generated method stub
    //             pub.subscribe(new DelegeateSub(s){
    //                 int sum = 0;
    //                 @Override
    //                 public void onNext(Integer i){
    //                     sum += i;
    //                 }

    //                 @Override
    //                 public void onComplete(){
    //                     s.onNext(sum);
    //                     s.onComplete();
    //                 }
    //             });
                
    //         }
            
    //     };
    // }

    //T -> R로 변환하는 Publisher
    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f){
        return new Publisher<R>(){
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegeateSub<T, R>(sub){
                    @Override
                    public void onNext(T i){
                        sub.onNext(f.apply(i));
                    }
                });
            }
        };
    }
        

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                logger.debug("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T integer) {
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
