package com.diy.computer.common;

/**
 * 工具类，获取当前登录用户id，基于ThreadLocal
 */
public class BaseContext {
//    在线程中穿行
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 设置id
     * @param id
     */
    public static void setId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 返回id
     * @return
     */
    public static Long getId() {
        return threadLocal.get();
    }

    public static void setCurrentId(Long userId) {
        threadLocal.set(userId);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
