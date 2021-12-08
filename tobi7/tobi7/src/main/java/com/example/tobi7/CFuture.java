package com.example.tobi7;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Future는 비동기 작업의 결과를 담고있는 객체
 */

@Slf4j
public class CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService es = Executors.newFixedThreadPool(10);

        CompletableFuture
                .supplyAsync(() -> {
                    log.info("runAsync");
                    return 1;
                }, es)
                .thenCompose(s -> {
                    log.info("then apply {}", s);
                    return CompletableFuture.completedFuture(s + 1);
                })
                .thenApplyAsync(s2 -> {
                    log.info("then apply {}", s2);
                    return s2 * 3;
                }, es)
                .exceptionally(e -> -10)
                .thenAcceptAsync(s3 -> {
                    log.info("then accecpt {}", s3);
                }, es);

        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}
