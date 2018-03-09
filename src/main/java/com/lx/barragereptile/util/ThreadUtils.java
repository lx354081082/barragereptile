package com.lx.barragereptile.util;

import java.util.Map;
import java.util.Set;

public class ThreadUtils {
    /**
     * 是否有此名字的线程
     * 有 false
     * 无 true
     */
    public static synchronized boolean isRunnable(String threadName) {
        //返回所有活动线程的堆栈跟踪图。
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        Set<Thread> threads = allStackTraces.keySet();
        for (Thread t:threads) {
            String name = t.getName();
            if (threadName.equals(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据线程name中断该线程
     * 返回结果
     */
    public static synchronized boolean interruptThread(String threadName) {
        //返回所有活动线程的堆栈跟踪图。
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        Set<Thread> threads = allStackTraces.keySet();
        Boolean flg = false;
        for (Thread t:threads) {
            if (threadName.equals(t.getName())) {
                t.interrupt();
                flg = true;
            }
        }
        return flg;
    }

    /**
     * 根据线程id 中断该线程
     */
    public static synchronized boolean interruptThreadByThreadId(Long threadId) {
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        Set<Thread> threads = allStackTraces.keySet();
        for (Thread t:threads) {
            if (threadId.equals(t.getId())) {
                t.interrupt();
                return true;
            }
        }
        return false;
    }

    /**
     * 通过线程id 获取线程名字
     */
    public static synchronized String isRunnableByThreadId(Long threadid) {
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        Set<Thread> threads = allStackTraces.keySet();
        for (Thread t:threads) {
            if (threadid.equals(t.getId())) {
                return t.getName();
            }
        }
        return null;
    }
}
