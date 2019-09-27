package com.thread.singleton;

/**
 * 饿汉模式，  构造函数中不会有太多的处理，这样会照成加载过慢，  线程安全的
 */
public class SingletonExample6 {

    //  私有构造函数
    private SingletonExample6(){

    }
    // 单例对象
    private static SingletonExample6 instance = null;

    static {
        instance = new SingletonExample6();
    }
    // 静态工厂方法
    public static SingletonExample6 getInstance(){
        return instance;
    }

    public static void main(String[] args) {
        System.out.println(getInstance().hashCode());
        System.out.println(getInstance().hashCode());
    }
}
