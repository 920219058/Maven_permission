package com.thread.singleton;

/**
 * 懒汉模式， 线程安全的
 */
public class SingletonExample3 {

    //  私有构造函数
    private SingletonExample3(){

    }

    // 单例对象
    private static SingletonExample3 instance = null;  // 第一次使用时候初始化：懒汉模式，线程不安全的
    // 静态工厂方法
    public static SingletonExample3 getInstance(){
        if(instance == null){
            instance = new SingletonExample3();
        }
        return instance;
    }

}
