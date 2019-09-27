package com.thread.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CyclicBarrierExample {

    private static CyclicBarrier barrier = new CyclicBarrier(5,() -> {
        // 优先执行此代码
        log.info("runable");
    }); // 5个线程同步等待

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            final int threadNum = i;
            executorService.execute(() -> {
                try{
                    race(threadNum);
                }catch (Exception e){
                    log.error("has error.");
                }

            });
        }
    }

    public static void race(int threadNum) throws Exception{
        Thread.sleep(1000);
        log.info("{} is ready.", threadNum);
        barrier.await();
        log.info("{} countiune.",threadNum);
    }

}
