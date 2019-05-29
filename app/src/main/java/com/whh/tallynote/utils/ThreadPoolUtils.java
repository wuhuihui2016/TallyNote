package com.whh.tallynote.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/5/15.
 */

public class ThreadPoolUtils {
    /**
     * 测试线程池
     */
    private void testThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5));

        for (int i = 0; i < 15; i++) {
            MyTask myTask = new MyTask(i);
            System.out.println("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" +
                    executor.getQueue().size() + "，已执行玩别的任务数目：" + executor.getCompletedTaskCount());
        }
    }

    class MyTask implements Runnable {
        private int taskNum;

        public MyTask(int num) {
            this.taskNum = num;
        }

        @Override
        public void run() {
            System.out.println("正在执行task " + taskNum);
            try {
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("task " + taskNum + "执行完毕");
        }
    }
}
