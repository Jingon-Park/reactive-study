package com.example.demo.practice;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DelegeateSub<T, R> implements Subscriber<T>{

    Subscriber sub;
    public DelegeateSub(Subscriber<? super R> sub) {
        this.sub = sub;
        
    }
    
    @Override
    public void onSubscribe(Subscription s) {
        sub.onSubscribe(s);
    }
    @Override
    public void onNext(T i) {
        sub.onNext(i);
    }
    @Override
    public void onError(Throwable throwable) {
        sub.onError(throwable);
    }
    @Override
    public void onComplete() {
        sub.onComplete();
    }

    
}
