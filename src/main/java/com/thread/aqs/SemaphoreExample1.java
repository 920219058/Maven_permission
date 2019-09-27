package com.thread.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
public class SemaphoreExample1 {
    private final static int threadCount = 200;

    public static void main(String[] args) throws Exception {

        ExecutorService service = Executors.newCachedThreadPool();

        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        final Semaphore semaphore = new Semaphore(20);

        for (int i = 0; i < threadCount; i++){
            final int threadNum = i;
            service.execute(() ->{
                try{
                    // semaphore.acquire(); // 获取一个许可
                    semaphore.acquire(2); // 获取多个许可

                    test(threadNum);
                    // semaphore.release();// 释放一个许可
                    semaphore.release(2);// 释放多个许可
                }catch (Exception e){
                    log.error("{}",e);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        log.info("finish");
        service.shutdown();
    }

    public static void test(int threadNum) throws Exception{
        Thread.sleep(100);
        log.info("{}",threadNum);
    }

}
