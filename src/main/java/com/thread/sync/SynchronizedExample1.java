package com.thread.sync;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

@Slf4j
public class SynchronizedExample1 {

    public void test1(int j){
        synchronized (this){
            for (int i = 0; i < 10; i++){
                log.info("test1 ：{} - {}.",j , i);
            }
        }
    }

    public synchronized void test2(int j){
        for (int i = 0; i < 10; i++){
            log.info("test2:{}-{}.",j, i);
        }
    }

    public void test3(int j) throws Exception{
        for (int i = 0; i < 10; i++){
            sleep(300);
            log.info("test3: {} - {}.",j, i);
        }
    }

    public static void main(String[] args){
        SynchronizedExample1 example1 = new SynchronizedExample1();
        SynchronizedExample1 example2 = new SynchronizedExample1();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(() -> {
            try {
                example1.test3(1);
            }catch (Exception e){

            }
        });
        executorService.execute(() -> {
            try {
                example1.test3(2);
            }catch (Exception e){

            }
        });
    }
}
