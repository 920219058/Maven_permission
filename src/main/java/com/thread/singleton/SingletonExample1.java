package com.thread.singleton;

public class SingletonExample1 {

    //  私有够着函数
    private SingletonExample1(){

    }

    // 单例对象
    private static SingletonExample1 instance = null;  // 第一次使用时候初始化：懒汉模式，线程不安全的
    // 静态工厂方法
    public static SingletonExample1 getInstance(){
        if(instance == null){
            instance = new SingletonExample1();
        }
        return instance;
    }

}
