package com.easy.wheel.scheduler;

import java.util.concurrent.ScheduledFuture;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author Mr.Yuxd
 * @Date 2022/11/15
 * @Version 1.0
 */
@Service
@Slf4j
public class CommonTask {

    @Resource(name = "myThreadPoolTaskScheduler")
    private ThreadPoolTaskScheduler taskScheduler;

    private ScheduledFuture future;

    public void startTask(){
        //每次调用前,可执行一次关闭之前的
        stop();
        /* *
         *  ThreadPoolTaskScheduler 内部方法非常丰富，本文实现的是一种corn表达式，周期执行
         *  1，schedule(Runnable task, Trigger trigger) corn表达式，周期执行
         *  2，schedule(Runnable task, Date startTime)  定时执行
         *  3，scheduleAtFixedRate(Runnable task, Date startTime, long period) 定时周期间隔时间执行。间隔时间单位 TimeUnit.MILLISECONDS
         *  4，scheduleAtFixedRate(Runnable task, long period) 间隔时间执行。单位毫秒
         */
        String cron = "0/1 * * * * ?";
        //RunSameThing 为执行的业务逻辑
        future = taskScheduler.schedule(new RunSameThing(), new CronTrigger(cron));
    }

    public void stop() {
        if (future != null) {
            log.info("我关闭了");
            future.cancel(true);
        }
    }

}