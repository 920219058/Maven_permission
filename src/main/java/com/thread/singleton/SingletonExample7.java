package com.thread.singleton;

/**
 * 枚举类型：最安全的
 */
// @Recommend
public class SingletonExample7 {
    private SingletonExample7(){

    }

    public static SingletonExample7 getInstance(){
        return Singleton.INSTANCE.getSingleton();
    }

    private enum Singleton {
        INSTANCE;

        private SingletonExample7 singleton;
        // jvm 保证这个方法绝对只调用一次
        Singleton(){
            singleton = new SingletonExample7();
        }
         public SingletonExample7 getSingleton(){
            return singleton;
         }
    }

}
