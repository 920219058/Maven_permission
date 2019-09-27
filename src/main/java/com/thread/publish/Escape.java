package com.thread.publish;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class Escape {

    private int thisCanbeEscape = 0;

    public Escape (){
        new InnerClass();
    }
    private class InnerClass{
         public InnerClass(){
            log.info("{}.",Escape.this.thisCanbeEscape);
         }
    }

    public static void main(String[] args) {
        new Escape();
    }
}
