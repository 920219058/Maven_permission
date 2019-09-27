package com.thread.threadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ThreadPoolExmaple3 {

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("task: {}",index);
                }
            });
        }
        service.shutdown();
    }

}
