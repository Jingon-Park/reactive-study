package com.example.tobi7;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Future는 비동기 작업의 결과를 담고있는 객체
 */

@Slf4j
public class CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CompletableFuture
                .supplyAsync(() -> {
                    log.info("runAsync");
                    return 1;
                })
                .thenApply(s -> {
                    log.info("then apply {}", s);
                    return s + 1;
                })
                .thenApply(s2 -> {
                    log.info("then apply {}", s2);
                    return s2 * 3;
                })
                .thenAccept(s3 -> {
                    log.info("then accecpt {}", s3);
                });

        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}
