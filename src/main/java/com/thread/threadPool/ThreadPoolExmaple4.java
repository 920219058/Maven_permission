package com.thread.threadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPoolExmaple4 {

    public static void main(String[] args) {
        /*ScheduledExecutorService service = Executors.newScheduledThreadPool(5);


        for (int i = 0; i < 10; i++) {
            final int index = i;
            service.schedule(new Runnable() {
                @Override
                public void run() {
                    log.info("task: {}",index);
                }
            },3, TimeUnit.MILLISECONDS);
        }
        service.shutdown();*/

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.warn("time run");
            }
        },new Date(),5*1000);
    }

}
