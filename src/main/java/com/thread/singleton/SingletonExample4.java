package com.thread.singleton;

/**
 * 懒汉模式， 不是线程安全的（jvm和cpu会发生指令重排的情况会法制先后才能不安全的），性能比Example好
 */
public class SingletonExample4 {

    //  私有构造函数
    private SingletonExample4(){

    }

    // 单例对象
    private static SingletonExample4 instance = null;  // 第一次使用时候初始化：懒汉模式，线程不安全的
    // 静态工厂方法
    public static SingletonExample4 getInstance(){
        if(instance == null){ // 双重检测机制
            synchronized (SingletonExample4.class){ //同步锁
                if(instance == null ){
                    instance = new SingletonExample4();
                }
            }
        }
        return instance;
    }

}
