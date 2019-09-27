package com.thread.singleton;

/**
 * 饿汉模式，  构造函数中不会有太多的处理，这样会照成加载过慢，  线程安全的
 */
public class SingletonExample2 {

    //  私有构造函数
    private SingletonExample2(){

    }

    // 单例对象
    private static SingletonExample2 instance = new SingletonExample2();
    // 静态工厂方法
    public static SingletonExample2 getInstance(){
        return instance;
    }

}
