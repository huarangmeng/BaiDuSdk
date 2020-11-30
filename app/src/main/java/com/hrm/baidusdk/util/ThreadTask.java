package com.hrm.baidusdk.util;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: Hrm
 * @description: 线程工具类
 * @data: 2020/11/20
 */
public class ThreadTask {
    /**
     * 线程任务实例
     */
    private static ThreadTask instance;

    /**
     * 网络线程最大数量
     */
    private final int netThreadCount = 5;

    /**
     * 数据库线程最大数量
     */
    private final int dbThreadCount = 3;

    /**
     * 其他类型的耗时线程数量
     */
    private final int otherThreadCount = 10;

    /**
     * 网络线程池
     */
    private ThreadPoolExecutor netThreadPool;

    /**
     * 数据库线程池
     */
    private ThreadPoolExecutor dbThreadPool;

    /**
     * 其他耗时操作线程池
     */
    private ThreadPoolExecutor otherThreadPool;

    /**
     * 网络线程队列
     */
    private PriorityBlockingQueue netThreadQueue;

    /**
     * 数据库线程队列
     */
    private PriorityBlockingQueue dbThreadQueue;

    /**
     * 其他线程队列
     */
    private PriorityBlockingQueue otherThreadQueue;

    /**
     * 任务比较
     */
    private Comparator<PriorTask> taskComparator;

    private ThreadTask() {
        final long keepAliveTime = 60L;
        taskComparator = new TaskCompare();

        netThreadQueue = new PriorityBlockingQueue<PriorTask>(netThreadCount, taskComparator);
        dbThreadQueue = new PriorityBlockingQueue<PriorTask>(dbThreadCount, taskComparator);
        otherThreadQueue = new PriorityBlockingQueue<PriorTask>(otherThreadCount, taskComparator);

        netThreadPool = new ThreadPoolExecutor(netThreadCount, netThreadCount, 0L, TimeUnit.MILLISECONDS, netThreadQueue);
        dbThreadPool = new ThreadPoolExecutor(dbThreadCount, dbThreadCount, 0L, TimeUnit.MILLISECONDS, dbThreadQueue);
        otherThreadPool = new ThreadPoolExecutor(otherThreadCount, Integer.MAX_VALUE, keepAliveTime, TimeUnit.SECONDS, otherThreadQueue);
    }

    /**
     * 获取线程管理实例
     *
     * @return 线程管理实例
     */
    public static ThreadTask getInstance() {
        if (instance == null) {
            instance = new ThreadTask();
        }
        return instance;
    }

    /**
     * 获取网络线程池
     *
     * @return
     */
    public ThreadPoolExecutor getNetThreadPool() {
        return netThreadPool;
    }

    /**
     * 执行数据库线程
     *
     * @param task     需要执行的任务
     * @param priority 优先级
     */
    public void executorDBThread(Runnable task, int priority) {
        if (!dbThreadPool.isShutdown()) {
            dbThreadPool.execute(new PriorTask(priority, task));
        }
    }

    /**
     * 执行数据库线程
     *
     * @param task 需要执行的任务
     */
    public void executorDBThread(Runnable task) {
        if (!dbThreadPool.isShutdown()) {
            dbThreadPool.execute(new PriorTask(ThreadPeriod.PERIOD_LOW, task));
        }
    }

    /**
     * 执行网络线程
     *
     * @param task     需要执行的任务
     * @param priority 优先级
     */
    public void executorNetThread(Runnable task, int priority) {
        if (!netThreadPool.isShutdown()) {
            netThreadPool.execute(new PriorTask(priority, task));
        }
    }

    /**
     * 执行网络线程
     *
     * @param task 需要执行的任务
     */
    public void executorNetThread(Runnable task) {
        if (!netThreadPool.isShutdown()) {
            netThreadPool.execute(new PriorTask(ThreadPeriod.PERIOD_LOW, task));
        }
    }

    /**
     * 执行除数据库之外的其他耗时任务
     *
     * @param task     需要执行的任务
     * @param priority 优先级
     */
    public void executorOtherThread(Runnable task, int priority) {
        if (!otherThreadPool.isShutdown()) {
            otherThreadPool.execute(new PriorTask(priority, task));
        }
    }

    /**
     * 执行除数据库之外的其他耗时任务
     *
     * @param task 需要执行的任务
     */
    public void executorOtherThread(Runnable task) {
        if (!otherThreadPool.isShutdown()) {
            otherThreadPool.execute(new PriorTask(ThreadPeriod.PERIOD_LOW, task));
        }
    }

    /**
     * 结束掉所有线程，并且回收（正在进行的有可能结束不掉）
     */
    public void shutDownAll() {
        netThreadPool.shutdown();
        dbThreadPool.shutdown();
        otherThreadPool.shutdown();
        instance = null;
    }

    /**
     * 优先级任务
     */
    public static class PriorTask implements Runnable {
        private int priori;

        private Runnable task;

        public PriorTask(int priority, Runnable runnable) {
            priori = priority;
            task = runnable;
        }

        public int getPriori() {
            return priori;
        }

        public void setPriori(int priori) {
            this.priori = priori;
        }

        public Runnable getTask() {
            return task;
        }

        public void setTask(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            if (task != null) {
                task.run();
            }
        }
    }


    /**
     * 任务比较器
     */
    public static class TaskCompare implements Comparator<PriorTask> {

        @Override
        public int compare(PriorTask o1, PriorTask o2) {
            return o1.getPriori() - o2.getPriori();
        }
    }

    /**
     * 线程优先级
     */
    public static class ThreadPeriod {
        /**
         * 线程优先级 低
         */
        public static final int PERIOD_LOW = 1;

        /**
         * 线程优先级 中
         */
        public static final int PERIOD_MIDDLE = 5;

        /**
         * 线程优先级 高
         */
        public static final int PERIOD_HIGH = 10;
    }
}
