package com.example.demo.reactive;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import lombok.extern.slf4j.Slf4j;


/**
 * 비동기의 작업의 결과를 가져오는 기본적인 방법
 * 1. Future를 이용한다.
 * 2. Callback을 활용한다.
 */
@Slf4j
public class FutureEx {

    interface SuccessCallback{

        void onSuccess(String result);
    }

    interface ExceptionCallback {

        void onError(Throwable throwable);

    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback sc;
        ExceptionCallback ec;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc,
            ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try{
                sc.onSuccess(get());
            } catch(InterruptedException e){
                Thread.currentThread().interrupt();

            } catch (ExecutionException e){
                ec.onError(e.getCause());
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService ec = Executors.newCachedThreadPool();

        CallbackFutureTask f = new CallbackFutureTask(()->{
            Thread.sleep(2000);
            if (1 == 1) throw new RuntimeException("Async ERROR!!");

            log.info("Async");
            return "Hello";
        },
            s-> System.out.println("Result: " + s),
            e-> System.out.println("Error: " + e.getMessage()));

        ec.execute(f);
        ec.shutdown();


    }

}
