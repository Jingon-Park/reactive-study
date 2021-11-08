package com.example.demo.reactive;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FutureEx {

    interface SuccessCallback{

        void onSuccess(String result);
    }

    interface ExceptionCallback {

        void onException(Throwable throwable);

    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback sc;
        ExceptionCallback ec;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc,
            ExceptionCallback ec) {
            super(callable);
            this.sc = sc;
            this.ec = ec;
        }
    }

    public static void main(String[] args) {
        ExecutorService es = Executors.newCachedThreadPool();

        
    }

}
