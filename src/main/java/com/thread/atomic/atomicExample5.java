package com.thread.atomic;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j

public class atomicExample5 {
    // 指定某个类 某个字段原子性更新
    private static AtomicIntegerFieldUpdater<atomicExample5> updater =
            AtomicIntegerFieldUpdater.newUpdater(atomicExample5.class,"count");

    @Getter
    public volatile int count = 100;

    private static atomicExample5 example5 = new atomicExample5();

    public static void main(String[] args) {
        if(updater.compareAndSet(example5,100,120)) {
            log.info("update success 1, {}. " , example5.getCount());
        }
        if( updater.compareAndSet(example5,100,120)){
            log.info("update success 2, {}." , example5.getCount());
        }else {
            log.info("update failed, {}.",example5.getCount());
        }
    }
}

