package com.easy.wheel.concurrence;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

/**
 * @Description
 * 当调用线程池execute() 方法添加一个任务时，线程池会做如下判断：
 * 如果有空闲线程，则直接执行该任务；
 * 如果没有空闲线程，且当前运行的线程数少于corePoolSize，则创建新的线程执行该任务；
 * 如果没有空闲线程，且当前的线程数等于corePoolSize，同时阻塞队列未满，则将任务入队列，而不添加新的线程；
 * 如果没有空闲线程，且阻塞队列已满，同时池中的线程数小于maximumPoolSize ，则创建新的线程执行任务；
 * 如果没有空闲线程，且阻塞队列已满，同时池中的线程数等于maximumPoolSize ，则根据构造函数中的 handler 指定的策略来拒绝新的任务。
 * @Author Mr.Yxd
 * @Date 2022/5/31
 * @Version 1.0
 */
@Component
public class ConcurrenceThreadPoolManager implements BeanFactoryAware {

    // 线程池维护线程的最少数量
    private final static int CORE_POOL_SIZE = 2;
    // 线程池维护线程的最大数量
    private final static int MAX_POOL_SIZE = 10;
    // 线程池维护线程所允许的空闲时间
    private final static int KEEP_ALIVE_TIME = 0;
    // 线程池所使用的缓冲队列大小
    private final static int WORK_QUEUE_SIZE = 50;

    /**
     * 用于储存在队列中的订单,防止重复提交,在真实场景中，可用redis代替 验证重复
     */
    Map<String, Object> cacheMap = new ConcurrentHashMap<>();

    /**
     * 订单的缓冲队列,当线程池满了，则将订单存入到此缓冲队列
     */
    Queue<Object> msgQueue = new LinkedBlockingQueue<Object>();

    //用于从IOC里取对象
    //如果实现Runnable的类是通过spring的application.xml文件进行注入,可通过 factory.getBean()获取，这里只是提一下
    private BeanFactory factory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        factory = beanFactory;
    }

    /**
     * 当线程池的容量满了，执行下面代码，将订单存入到缓冲队列
     */
    final RejectedExecutionHandler handler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            //订单加入到缓冲队列
            msgQueue.offer(((BusinessThread) r).getAcceptStr());
            System.out
                .println("系统任务太忙了,把此订单交给(调度线程池)逐一处理，订单号：" + ((BusinessThread) r).getAcceptStr());
        }
    };


    /**
     * 创建线程池
     */
    final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
        KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue(WORK_QUEUE_SIZE), this.handler);


    /**
     * 将任务加入订单线程池
     */
    public void addOrders(String orderId) {
        System.out.println("此订单准备添加到线程池，订单号：" + orderId);
        //验证当前进入的订单是否已经存在
        if (cacheMap.get(orderId) == null) {
            cacheMap.put(orderId, new Object());
            BusinessThread businessThread = new BusinessThread(orderId);
            threadPool.execute(businessThread);
        }
    }

    /**
     * 线程池的定时任务----> 称为(调度线程池)。此线程池支持定时以及周期性执行任务的需求。
     */
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);


    /**
     * 检查(调度线程池)，每秒执行一次，查看订单的缓冲队列是否有 订单记录，则重新加入到线程池
     */
    final ScheduledFuture scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
            //判断缓冲队列是否存在记录
            if (!msgQueue.isEmpty()) {
                //当线程池的队列容量少于WORK_QUEUE_SIZE，则开始把缓冲队列的订单 加入到 线程池
                if (threadPool.getQueue().size() < WORK_QUEUE_SIZE) {
                    String orderId = (String) msgQueue.poll();
                    BusinessThread businessThread = new BusinessThread(orderId);
                    threadPool.execute(businessThread);
                    System.out.println("(调度线程池)缓冲队列出现订单业务，重新添加到线程池，订单号：" + orderId);
                }
            }
        }
    }, 0, 1, TimeUnit.SECONDS);


    /**
     * 获取消息缓冲队列
     */
    public Queue<Object> getMsgQueue() {
        return msgQueue;
    }

    /**
     * 终止订单线程池+调度线程池
     */
    public void shutdown() {
        //true表示如果定时任务在执行，立即中止，false则等待任务结束后再停止
        System.out.println("终止订单线程池+调度线程池：" + scheduledFuture.cancel(false));
        scheduler.shutdown();
        threadPool.shutdown();
    }
}